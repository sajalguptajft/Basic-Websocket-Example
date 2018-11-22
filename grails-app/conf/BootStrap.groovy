import testspringws.Chat
import testspringws.User

class BootStrap {

    def init = { servletContext ->

        User user = new User()
        Chat chat = new Chat()

        user.username = "sajal"
        user.save(flush: true)
        chat.addToUser(user)

        user = new User()
        user.username = "rohit"
        user.save(flush: true)
        chat.addToUser(user)

        chat.chatName = "hello"
        chat.save(flush: true)

    }
    def destroy = {
    }
}
