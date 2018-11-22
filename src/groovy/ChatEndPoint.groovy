import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import testspringws.Chat
import testspringws.ChatMessages
import testspringws.User

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/chat/{chatId}")
@WebListener
class ChatroomEndpoint implements ServletContextListener {

    private static final Set<Session> users = ([] as Set).asSynchronized()

    @Override
    void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.servletContext
        ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
        try {
            if (Environment.current == Environment.DEVELOPMENT) {
                serverContainer.addEndpoint(ChatroomEndpoint)
            }
            ApplicationContext ctx = (ApplicationContext) servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
            GrailsApplication grailsApplication = ctx.grailsApplication
            serverContainer.defaultMaxSessionIdleTimeout = grailsApplication.config.servlet.defaultMaxSessionIdleTimeout ?: 0
        } catch (IOException e) {
        }
    }

    @Override
    void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    @OnOpen
    public void onOpen(Session userSession, @PathParam("chatId") final String chatId) {
        userSession.userProperties.put("chatId", chatId) // id of the chat (Project) ?
        users.add(userSession)
    }

    @OnMessage
    public void onMessage(String message, Session userSession) {
        try {
            Chat.withTransaction {
                Chat chat = Chat.findById(Integer.valueOf(userSession.userProperties.get("chatId").toString()))
                ChatMessages chatMessages = new ChatMessages()
                chatMessages.user = User.findById(2) // get from springSecurityService.currentUser
                chatMessages.chat = chat
                chatMessages.message = message
                chat.addToChatMessages(chatMessages)
                chatMessages.save(flush: true, failOnError: true)
            }
        } catch (e) {
            println e.localizedMessage
        }
        Iterator<Session> iterator = users.iterator()
        while (iterator.hasNext()) {
            iterator.next().basicRemote.sendText(message)
        }
    }

    @OnClose
    public void onClose(Session userSession, CloseReason closeReason) {
        users.remove(userSession)
        userSession.close(closeReason)
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace()
    }

}
