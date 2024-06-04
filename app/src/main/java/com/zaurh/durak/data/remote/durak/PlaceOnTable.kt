package com.zaurh.durak.data.remote.durak

data class PlaceOnTable(
    val first: CardPair? = null,
    val firstAttack: CardPair? = null,
    val second: CardPair? = null,
    val secondAttack: CardPair? = null,
    val third: CardPair? = null,
    val thirdAttack: CardPair? = null,
    val fourth: CardPair? = null,
    val fourthAttack: CardPair? = null,
    val fifth: CardPair? = null,
    val fifthAttack: CardPair? = null,
    val sixth: CardPair? = null,
    val sixthAttack: CardPair? = null,
) {
    fun toMap() = mapOf(
        "first" to first,
        "firstAttack" to firstAttack,
        "second" to second,
        "secondAttack" to secondAttack,
        "third" to third,
        "thirdAttack" to thirdAttack,
        "fourth" to fourth,
        "fourthAttack" to fourthAttack,
        "fifth" to fifth,
        "fifthAttack" to fifthAttack,
        "sixth" to sixth,
        "sixthAttack" to sixthAttack,
    )
}
