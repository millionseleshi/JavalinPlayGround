import config.AppConfig
import org.h2.tools.Server


fun main(args: Array<String>) {
    Server.createWebServer().start()
    AppConfig().setUp().start()
}