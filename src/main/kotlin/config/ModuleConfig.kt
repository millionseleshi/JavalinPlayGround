/*
 * @Author Million Seleshi
 *  2021.
 */

package config

import controller.UserController
import org.koin.dsl.module
import repository.UserRepository
import router.ApiRoute
import service.UserService

object ModuleConfig {
    private val configModule = module {
        single { AppConfig() }
        single {
            DbConfig(getProperty("jdbc.url"), getProperty("db.username"), getProperty("db.password")).get()
        }
        single { ApiRoute(get()) }
    }

    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get()) }
        single { UserRepository(get()) }
    }

     val allModule = listOf(configModule, userModule)
}