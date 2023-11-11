package ru.astrainteractive.astrarating.feature.changerating.di

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteOnPlayerRepository
import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteOnPlayerRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteTodayRepository
import ru.astrainteractive.astrarating.feature.changerating.data.CanVoteTodayRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.InsertRatingRepository
import ru.astrainteractive.astrarating.feature.changerating.data.InsertRatingRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.InsertUserRepository
import ru.astrainteractive.astrarating.feature.changerating.data.InsertUserRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.PlatformBridge
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteOnPlayerUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteOnPlayerUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteTodayUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteTodayUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CanVoteUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckEnoughTimeUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckEnoughTimeUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckPlayerExistsUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.CheckPlayerExistsUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertRatingUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertUserUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertUserUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.ValidateMessageUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.ValidateMessageUseCaseImpl
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface ChangeRatingModule {
    val canVoteOnPlayerRepository: CanVoteOnPlayerRepository
    val canVoteTodayRepository: CanVoteTodayRepository
    val insertRatingRepository: InsertRatingRepository
    val insertUserRepository: InsertUserRepository

    val addRatingUseCase: AddRatingUseCase
    val canVoteOnPlayerUseCase: CanVoteOnPlayerUseCase
    val canVoteTodayUseCase: CanVoteTodayUseCase
    val canVoteUseCase: CanVoteUseCase
    val checkEnoughTimeUseCase: CheckEnoughTimeUseCase
    val checkPlayerExistsUseCase: CheckPlayerExistsUseCase
    val insertRatingUseCase: InsertRatingUseCase
    val insertUserUseCase: InsertUserUseCase
    val validateMessageUseCase: ValidateMessageUseCase

    class Default(
        private val dbApi: RatingDBApi,
        private val empireConfig: Reloadable<EmpireConfig>,
        private val dispatchers: KotlinDispatchers,
        private val platformBridge: Provider<PlatformBridge>
    ) : ChangeRatingModule {
        override val canVoteOnPlayerRepository: CanVoteOnPlayerRepository by Provider {
            CanVoteOnPlayerRepositoryImpl(dbApi, dispatchers)
        }
        override val canVoteTodayRepository: CanVoteTodayRepository by Provider {
            CanVoteTodayRepositoryImpl(dbApi, dispatchers)
        }
        override val insertRatingRepository: InsertRatingRepository by Provider {
            InsertRatingRepositoryImpl(dbApi, dispatchers)
        }
        override val insertUserRepository: InsertUserRepository by Provider {
            InsertUserRepositoryImpl(dbApi, dispatchers)
        }
        override val addRatingUseCase: AddRatingUseCase by Provider {
            AddRatingUseCaseImpl(
                canVoteOnPlayerUseCase = canVoteOnPlayerUseCase,
                canVoteTodayUseCase = canVoteTodayUseCase,
                canVoteUseCase = canVoteUseCase,
                checkEnoughTimeUseCase = checkEnoughTimeUseCase,
                checkPlayerExistsUseCase = checkPlayerExistsUseCase,
                validateMessageUseCase = validateMessageUseCase,
                insertRatingUseCase = insertRatingUseCase
            )
        }
        override val canVoteOnPlayerUseCase: CanVoteOnPlayerUseCase by Provider {
            CanVoteOnPlayerUseCaseImpl(
                maxRatingPerPlayer = empireConfig.value.maxRatingPerPlayer,
                canVoteOnPlayerRepository = canVoteOnPlayerRepository
            )
        }
        override val canVoteTodayUseCase: CanVoteTodayUseCase by Provider {
            CanVoteTodayUseCaseImpl(
                maxRatingPerDay = empireConfig.value.maxRatingPerDay,
                canVoteTodayRepository = canVoteTodayRepository
            )
        }
        override val canVoteUseCase: CanVoteUseCase by Provider {
            CanVoteUseCaseImpl()
        }
        override val checkEnoughTimeUseCase: CheckEnoughTimeUseCase by Provider {
            CheckEnoughTimeUseCaseImpl(platformBridge.provide())
        }
        override val checkPlayerExistsUseCase: CheckPlayerExistsUseCase by Provider {
            CheckPlayerExistsUseCaseImpl(platformBridge.provide())
        }
        override val insertRatingUseCase: InsertRatingUseCase by Provider {
            InsertRatingUseCaseImpl(
                insertUserUseCase = insertUserUseCase,
                insertRatingRepository = insertRatingRepository
            )
        }
        override val insertUserUseCase: InsertUserUseCase by Provider {
            InsertUserUseCaseImpl(insertUserRepository = insertUserRepository)
        }
        override val validateMessageUseCase: ValidateMessageUseCase by Provider {
            ValidateMessageUseCaseImpl(
                minMessageLength = empireConfig.value.minMessageLength,
                maxMessageLength = empireConfig.value.maxMessageLength
            )
        }
    }
}
