package testspringws

class HomeController {

    def index() {
        Date todayDate = new Date()
        def previousChats = ChatMessages.createCriteria().list {
            eq ("chat", Chat.get(1))
            between ("dateCreated", todayDate-2, todayDate)
        }
        render view: '/index', model: [previousChats: previousChats]
    }
}
