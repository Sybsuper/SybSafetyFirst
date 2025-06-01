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
    private val enabledModules = mutableSetOf<Module>()
    val modules = listOf<Module>(
        HungryMode,
        IntentionalGameDesign,
        NoF3,
        FastCreepers,
        WaterCurrent,
        Wildfire,
        HeavyArmor,
        HostileReinforcements
    )
    private val nameMap = modules.associateBy { it.name }
    private val idMap = modules.associateBy { it.id }

    init {
        val config = SybSafetyFirst.instance.config
        modules.forEach {
            val options = loadOptions(it)
            if (it.options.enabled) {
                enableModule(it)
            }
        }
    }

    fun reloadModule(module: Module) {
        if (module in enabledModules) {
            disableModule(module)
        }
        enableModule(module)
    }

    fun enableModule(module: Module) {
        if (module in enabledModules) return
        module.options = loadOptions(module)
        enabledModules.add(module)
        module.onEnable()
        Bukkit.getPluginManager().registerEvents(module, SybSafetyFirst.instance)
    }

    @OptIn(InternalSerializationApi::class)
    private fun loadOptions(module: Module): ModuleOptions {
        val moduleFolder = File(SybSafetyFirst.instance.dataFolder, "modules")
        if (!moduleFolder.exists() || !moduleFolder.isDirectory) {
            moduleFolder.mkdirs()
        }
        val moduleConfig = moduleFolder.resolve("${module.id}.yml")
        return runCatching {
            Yaml.default.decodeFromStream(
                module.options::class.serializer(),
                moduleConfig.inputStream()
            )
        }.getOrElse { error ->
            SybSafetyFirst.instance.logger.warning("Failed to load options for module ${module.name}: ${error.message}")
            module.options.also {
                runCatching {
                    Yaml.default.encodeToStream<ModuleOptions>(
                        it::class.serializer() as KSerializer<in ModuleOptions>,
                        it,
                        moduleConfig.outputStream()
                    )
                }.onFailure { error ->
                    SybSafetyFirst.instance.logger.warning("Failed to save default options for module ${module.name}: ${error.message}")
                }
            }
        }.also {
            SybSafetyFirst.instance.logger.info("Loaded options for module ${module.name}: $it")
        }
    }

    fun disableModule(module: Module) {
        if (module !in enabledModules) return
        HandlerList.unregisterAll(module)
        module.onDisable()
    }

    fun fromName(moduleName: String) = nameMap[moduleName]
    fun fromId(moduleId: String) = idMap[moduleId]
    fun isEnabled(module: Module): Boolean = module in enabledModules
}