package software.amazon.test

import com.plusmobileapps.kotlin.inject.anvil.extensions.assistedfactory.runtime.ContributesAssistedFactory
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Assisted
interface Base
@Inject
@ContributesAssistedFactory(
    scope = Unit::class,
    assistedFactory = BaseFactory::class,
)
class Impl(
    @Assisted val id: String,
) : Base
interface BaseFactory {
    fun create(id: String): Base
}