package cash.z.ecc.android.sdk.type

/**
 * Data structure to hold the total and available balance of the wallet. This is what is
 * received on the balance channel.
 *
 * @param totalZatoshi the total balance, ignoring funds that cannot be used.
 * @param availableZatoshi the amount of funds that are available for use. Typical reasons that funds
 * may be unavailable include fairly new transactions that do not have enough confirmations or
 * notes that are tied up because we are awaiting change from a transaction. When a note has
 * been spent, its change cannot be used until there are enough confirmations.
 */
data class WalletBalance(
    val totalZatoshi: Long = -1,
    val availableZatoshi: Long = -1
)

/**
 * Model object for holding a wallet birthday.
 *
 * @param height the height at the time the wallet was born.
 * @param hash the hash of the block at the height.
 * @param time the block time at the height. Represented as seconds since the Unix epoch.
 * @param tree the sapling tree corresponding to the height.
 */
data class WalletBirthday(
    val height: Int = -1,
    val hash: String = "",
    val time: Long = -1,
    val tree: String = ""
)

/**
 * A grouping of keys that correspond to a single wallet account but do not have spend authority.
 *
 * @param extfvk the extended full viewing key which provides the ability to see inbound and
 * outbound shielded transactions. It can also be used to derive a z-addr.
 * @param extpub the extended public key which provides the ability to see transparent
 * transactions. It can also be used to derive a t-addr.
 */
data class UnifiedViewingKey(
    val extfvk: String = "",
    val extpub: String = ""
)