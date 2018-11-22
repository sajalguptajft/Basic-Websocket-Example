package testspringws

class ChatMessages {

    Chat chat
    User user
    String message
    Date lastUpdated
    Date dateCreated

    static mapping = {
        message type: 'text'
    }

    static constraints = {
    }
}
