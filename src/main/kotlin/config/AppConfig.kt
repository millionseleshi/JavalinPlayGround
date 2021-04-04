package config

import config.ModuleConfig.allModule
import io.javalin.Javalin
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
        }.events {
            it.serverStopping {
                stopKoin()
            }
        }
        apiRouter.register(app)
        return app
    }
}

