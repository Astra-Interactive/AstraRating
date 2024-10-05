package ru.astrainteractive.astrarating.core.util

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.util.StringDescExt.plus

object StringDescExt {
    operator fun StringDesc.plus(other: String): StringDesc.Raw {
        return when (this) {
            is StringDesc.Raw -> StringDesc.Raw(raw.plus(other))
        }
    }

    operator fun StringDesc.plus(other: StringDesc): StringDesc.Raw {
        return when (other) {
            is StringDesc.Raw -> plus(other.raw)
        }
    }
}
