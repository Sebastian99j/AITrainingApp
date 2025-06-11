import Combine
import shared
import Foundation

class ExerciseViewModel: ObservableObject {
    let controller: ExerciseController

    @Published var series: [ExerciseSeries] = []
    @Published var exercises: [String] = []
    @Published var recommendation: String?
    @Published var feedbackSent: Bool = false

    init(
        repository: ExerciseRepository,
        userRepo: UserLocalRepository,
        typeRepo: TrainingTypeRepository,
        historyRepo: TrainingHistoryRepository,
        qLearningRepo: QLearningRepository,
        coroutineScope: Kotlinx_coroutines_coreCoroutineScope
    ) {
        self.controller = ExerciseController(
            repository: repository,
            userRepo: userRepo,
            typeRepo: typeRepo,
            historyRepo: historyRepo,
            qLearningRepo: qLearningRepo,
            coroutineScope: coroutineScope
        )

        refreshData()
    }

    func refreshData() {
        series = controller.getSeriesList()
        exercises = controller.getExercises()
        recommendation = controller.getRecommendation()
        feedbackSent = controller.isFeedbackSent()
    }

    var elapsedSeconds: Int32 {
        Int32(controller.getElapsedSeconds())
    }

    var timerRunning: Bool {
        controller.isTimerRunning()
    }

    // MARK: - Public API
    func toggleTimer() {
        controller.toggleTimer()
    }

    func setSelectedExercise(name: String) {
        controller.setSelectedExercise(name: name)
    }

    func addSeries(exercise: String, weight: Float, reps: Int32, sets: Int32, rpe: Int32) {
        controller.addSeries(
            exercise: exercise,
            weight: weight,
            reps: reps,
            sets: sets,
            rpe: rpe,
            duration: elapsedSeconds
        )
        refreshData() // aktualizacja po dodaniu
    }

    func totalWeight() -> Int32 {
        controller.totalWeight()
    }

    func totalReps() -> Int32 {
        controller.totalReps()
    }

    func totalSets() -> Int32 {
        controller.totalSets()
    }

    func averageDuration() -> Int32 {
        controller.averageDuration()
    }

    func fetchRecommendation() {
        controller.fetchRecommendation()
        refreshData()
    }

    func sendFeedback(successful: Bool) {
        controller.sendFeedback(successful: successful)
        refreshData()
    }

    func saveAll() {
        controller.saveAll()
        refreshData()
    }
}
