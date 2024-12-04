package ru.astrainteractive.astrarating.core.util

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.util.StringDescExt.plus

object StringDescExt {
    operator fun StringDesc.plus(other: String): StringDesc {
        return when (this) {
            is StringDesc.Raw -> StringDesc.Raw(raw.plus(other))
            is StringDesc.Plain -> StringDesc.Plain(raw.plus(other))
        }
    }

    operator fun StringDesc.plus(other: StringDesc): StringDesc {
        return when (other) {
            is StringDesc.Raw -> plus(other.raw)
            is StringDesc.Plain -> plus(other.raw)
        }
    }
}
