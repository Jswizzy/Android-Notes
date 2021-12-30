class App: Application() {

    companion object {
        private lateinit var instance: App

        val noteDao by lazy { NotesDatabase.getInstance(instance).notesDoa() }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
