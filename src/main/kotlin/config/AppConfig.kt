/*
 * @Author Million Seleshi
 *  2021.
 */

package config

import config.ModuleConfig.allModule
import exception.ExceptionMapper
import io.javalin.Javalin
import io.javalin.core.event.EventListener
import org.eclipse.jetty.server.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.fileProperties
import router.ApiRoute


class AppConfig : KoinComponent {

    private val apiRouter: ApiRoute by inject()
    fun setUp(): Javalin {
        startKoin {
            modules(allModule)
            fileProperties()
        }

        val app = Javalin.create { config ->
            config.apply {
                enableWebjars()
                enableCorsForAllOrigins()
                contextPath = getKoin().getProperty<String>("context") as String
                server {
                    Server(getKoin().getProperty<String>("server_port").toString().toInt())
                }
            }
        }
        ExceptionMapper.register(app)
        apiRouter.register(app)
        return app
    }

    fun stop() {
        val app = Javalin.create().stop()
        app.events { event: EventListener ->
            event.serverStopped {
                stopKoin()
            }
        }
    }


}

