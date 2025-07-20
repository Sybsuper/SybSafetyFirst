package com.sybsuper.sybsafetyfirst.modules

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import java.io.File

object ModuleManager {
    private val internalEnabledModuleIds = mutableSetOf<String>()
    val enabledModuleIds get() = internalEnabledModuleIds.toSet()
    val disabledModuleIds get() = modules.map { it.id }.toSet() - internalEnabledModuleIds
    val modules = listOf<Module>(
        HungryMode(),
        IntentionalGameDesign(),
        NoF3(),
        FastCreepers(),
        WaterCurrent(),
        Wildfire(),
        HeavyArmor(),
        HostileReinforcements(),
        BrokenBones(),
        HungerDelirium(),
        PlayerZombies(),
        LimitedCrafting(),
        LightningFires(),
        WrongToolsHurt(),
        NetherPortalsDestabilize(),
        SkillBasedInventory(),
        BadAirCaves(),
        BabyCreatures(),
        ExpensiveTrades()
    )
    private val currentModuleInstances = modules.associateBy { it.id }.toMutableMap()
    private val nameMap = modules.associateBy { it.name }
    private val idMap = modules.associateBy { it.id }

    init {
        val config = SybSafetyFirst.instance.config
        modules.forEach {
            val options = loadOptions(it)
            if (options.enabled) {
                enableWithOptions(it, options)
            }
        }
    }

    /**
     * Reload module by creating a new instance of it and enabling it.
     *
     * @param module the module to reload
     * @return the new instance of the module
     */
    fun reloadModule(module: Module): Result<Module> {
        if (module.id in internalEnabledModuleIds) {
            disableModule(module)
        }
        return enableModule(module)
    }

    private fun createNewInstance(module: Module): Module {
        val newModule = module::class.java.getDeclaredConstructor().newInstance() as Module
        currentModuleInstances[module.id] = newModule
        return newModule
    }

    fun enableModule(module: Module): Result<Module> {
        if (module.id in internalEnabledModuleIds) return Result.failure(IllegalStateException("Module ${module.name} is already enabled."))
        val options = loadOptions(module)
        return enableWithOptions(module, options)
    }

    private fun enableWithOptions(module: Module, options: ModuleOptions): Result<Module> {
        if (module.id in internalEnabledModuleIds) return Result.failure(IllegalStateException("Module ${module.name} is already enabled."))
        val newModule = createNewInstance(module)
        assert(newModule !== module) { "Module instance should be a new instance, not the same as the original." }
        HandlerList.unregisterAll(module)
        newModule.options = options
        @Suppress("DEPRECATION")
        newModule.options.enabled = true
        saveModuleOptions(newModule)
        internalEnabledModuleIds.add(newModule.id)
        newModule.onEnable()
        Bukkit.getPluginManager().registerEvents(newModule, SybSafetyFirst.instance)
        return Result.success(newModule)
    }

    @OptIn(InternalSerializationApi::class)
    private fun loadOptions(module: Module): ModuleOptions {
        val moduleConfig = moduleConfigFile(module)
        return runCatching {
            val stream = moduleConfig.inputStream()
            val result = Yaml.default.decodeFromStream(
                module.options::class.serializer(),
                stream
            )
            stream.close()
            result
        }.getOrElse { error ->
            SybSafetyFirst.instance.logger.warning("Failed to load options for module ${module.name}: ${error.message}")
            // Use the default options defined in the module
            val defaultOptions = module.options
            defaultOptions.also {
                runCatching {
                    val stream = moduleConfig.outputStream()
                    Yaml.default.encodeToStream<ModuleOptions>(
                        it::class.serializer() as KSerializer<in ModuleOptions>,
                        it,
                        stream
                    )
                    stream.close()
                }.onFailure { error ->
                    SybSafetyFirst.instance.logger.warning("Failed to save default options for module ${module.name}: ${error.message}")
                }
            }
        }.also {
            SybSafetyFirst.instance.logger.info("Loaded options for module ${module.name}: $it")
        }
    }

    fun disableModule(module: Module) {
        if (module.id !in internalEnabledModuleIds) return
        @Suppress("DEPRECATION")
        module.options.enabled = false
        HandlerList.unregisterAll(module)
        module.onDisable()
        internalEnabledModuleIds.remove(module.id)
        currentModuleInstances.remove(module.id)
    }

    @OptIn(InternalSerializationApi::class)
    fun saveModuleOptions(module: Module) {
        val moduleConfig = moduleConfigFile(module)
        runCatching {
            val stream = moduleConfig.outputStream()
            Yaml.default.encodeToStream<ModuleOptions>(
                module.options::class.serializer() as KSerializer<in ModuleOptions>,
                module.options,
                stream
            )
            stream.close()
        }.onFailure { error ->
            SybSafetyFirst.instance.logger.warning("Failed to save options for module ${module.name}: ${error.message}")
        }
    }

    private fun moduleConfigFile(module: Module): File {
        val moduleFolder = File(SybSafetyFirst.instance.dataFolder, "modules")
        if (!moduleFolder.exists() || !moduleFolder.isDirectory) {
            moduleFolder.mkdirs()
        }
        val moduleConfig = moduleFolder.resolve("${module.id}.yml")
        return moduleConfig
    }

    fun fromId(moduleId: String) = idMap[moduleId]
    fun isEnabled(module: Module): Boolean = module.id in internalEnabledModuleIds
    fun enabledInstanceFromId(id: String): Module? = currentModuleInstances[id]
}