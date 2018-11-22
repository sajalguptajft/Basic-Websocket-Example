package testspringws

class Chat {

    String chatName
    List user
    List chatMessages
    Date lastUpdated
    Date dateCreated

    static hasMany = [user: User, chatMessages: ChatMessages]

    static constraints = {
        chatName nullable: false
        user nullable: false
        chatMessages nullable: true
    }
}
