# TestFitnes

## Описание проекта
Приложение «TestFitnes» демонстрирует реализацию простого клиент-сервиса для просмотра списка тренировок и воспроизведения видео. Код разделён на четыре модуля:

- **app** — корневой Android-модуль (точка входа, содержит `MainActivity`, навигацию, доступные зависимости).  
- **presentation** — слой интерфейса (UI) и ViewModel’ей, построенный на архитектуре MVVM с использованием Jetpack Navigation, Media3 (ExoPlayer), Hilt и Coroutine Flow.  
- **data** — слой доступа к данным: Retrofit-сервис, ApiHelper для безопасных API-вызовов, RemoteDataSource, реализация репозитория. Тестируется через MockWebServer, Turbine и kotlinx-coroutines-test.  
- **domain** — чистый слой бизнес-логики: модели `Workout` и `VideoWorkout`, интерфейсы репозиториев, UseCase’ы и утилиты (`ResultState`, `WorkoutType`).  

## Архитектура и ключевые решения

### 1. Чистая многослойная архитектура (Clean / Hexagonal)
- **Domain**: содержит только бизнес-модели (`Workout`, `VideoWorkout`) и интерфейсы (`WorkoutRepository`). Здесь нет никаких Android-зависимостей. UseCase’ы (`GetWorkoutsUseCase`, `GetVideoWorkoutUseCase`) возвращают `Flow<ResultState<...>>` или `suspend`-методы, предоставляя единый формат ответов (успех, ошибка, загрузка).
- **Data**: отвечает за получение данных из сети (Retrofit).  
  - `ApiHelper.safeApiCall` оборачивает любые исключения (`HttpException`, `IOException`, `ApiException`) в единый тип `ApiResponse<T>`.  
  - `WorkoutRemoteDataSourceImpl` общается с `FitnessApiService` (Retrofit-интерфейс) и преобразует ответы в доменные модели через мапперы (`WorkoutResponse.toDomain()`, `VideoWorkoutResponse.toDomain()`).  
  - Репозиторий (`WorkoutRepositoryImpl`) конвертирует `ApiResponse` в `ResultState` (Success / Error).  
- **Presentation**:  
  - **ViewModel’и** (`WorkoutsViewModel`, `VideoWorkoutViewModel`) подписываются на UseCase’ы и формируют UI-состояние (через `StateFlow` для экрана списка и экрана видео).  
  - **MVVM + Hilt + Coroutine Flow**:  
    - ViewModelComponent (Hilt) подменяет реальные UseCase’ы на поддельные в тестах.  
    - `WorkoutsViewModel` хранит сразу несколько потоков (`isLoading`, `errorMessage`, `filteredWorkouts`), применяя debounce для поиска и комбинацию фильтрации по названию и типу.  
    - `VideoWorkoutViewModel` следит за загрузкой ссылки, управляет состоянием (Loading, Success, Error).  
  - **UI**: фрагменты используют View Binding, RecyclerView (Adapter + DiffUtil) для списка. На экране видео — `ExoPlayer` с кастомным контроллером и кнопкой выбора качества.  

### 2. Зависимости и DI
- **Dagger Hilt**:
  - Вставлен в каждый Android-модуль (app, data, presentation) через аннотацию `@HiltAndroidApp` (Application) и `@AndroidEntryPoint` (Activity, Fragments, ViewModel).  
  - Модули Hilt:  
    - `NetworkModule` (data): предоставляет `OkHttpClient`, `Retrofit`, `FitnessApiService`, `ApiHelper`.  
    - `RepositoryModule` (data): биндинг `WorkoutRepositoryImpl : WorkoutRepository`.  
    - `UseCaseModule` (presentation): биндинг `GetWorkoutsUseCase : GetWorkoutsUseCaseStub` и `GetVideoWorkoutUseCase : GetVideoWorkoutUseCaseStub`.  
- **Gradle Version Catalog (libs.versions.toml)**: все версии зависимостей централизованы, что упрощает обновления.  
- **ViewModelComponent scope** (presentation): UseCase’ы с областью `@ViewModelScoped`.  

### 3. Сетевые вызовы и безопасность
- **Retrofit 2 + OkHttp**:
  - Базовый URL задаётся через Hilt (строка `baseUrl`) и может переопределяться (`@Named("BASE_URL")`).  
  - **HttpLoggingInterceptor**: включён для DEBUG-сборки, логирует тело запросов/ответов.  
  - **Timeouts** (connect, read, write — 30 сек.), заданные через `OkHttpClient.Builder`.  
- **ApiHelper.safeApiCall**:  
  ```kotlin
  override suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
      return try {
          val result = apiCall()
          ApiResponse.Success(result)
      } catch (e: HttpException) {
          ApiResponse.Error(ApiException(e.code(), e.message(), e))
      } catch (e: IOException) {
          ApiResponse.Error(ApiException(null, "Network error", e))
      } catch (e: ApiException) {
          ApiResponse.Error(e)              
      } catch (e: Exception) {
          ApiResponse.Error(ApiException(null, e.localizedMessage ?: "Unknown error", e))
      }
  }

## Итоговый стек и архитектура

**Языки и инструменты:**
- Kotlin 1.8+
- Java 11
- Gradle 8
- Git

**Модульная структура:**
- `:domain` — чистый Kotlin/JVM, бизнес-логика, UseCase, ResultState, WorkoutType.  
- `:data` — Retrofit, OkHttp, ApiHelper, RemoteDataSource, репозиторий, мапперы, MockWebServer (тесты).  
- `:presentation` — MVVM, ViewModel, Hilt, Coroutine Flow, Turbine (тесты), фильтрация (дебаунс + сочетание поиска и типа).  
- `:app` — Hilt, MainActivity, Navigation, NoInternetFragment, BottomNavigation.

**DI:**
- Dagger Hilt во всех Android-модулях

**Сетевой слой:**
- Retrofit 2 + OkHttp3
- ApiHelper.safeApiCall
- мапперы DTO→Domain

**Воспроизведение видео:**
- ExoPlayer 2.19.1 / Media3 1.7.1
- выбор качества через TrackSelectionDialogBuilder

**Асинхронность и тестирование:**
- Coroutines 1.7.3
- Flow
- kotlinx-coroutines-test
- Turbine
- MockWebServer
- MockK

**CI:**
- GitHub Actions с проверкой сборки и запуском тестов

