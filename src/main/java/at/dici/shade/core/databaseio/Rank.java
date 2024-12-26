package at.dici.shade.core.databaseio;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public enum Rank {
    BOT_DEV("\u001B[1;30m","Shade Dev","#ffffff", 1, 1000),
    BOT_TESTER("\u001B[1;36m","Bot Tester","#00d5ce", 2, 400),
    BOT_VIP("\u001B[1;33m","VIP","#ff8811", 3, 0),
    BOT_TRANSLATOR("\u001B[1;36m","Translator","#00d5ce", 4, 500),
    BOT_PARTNER("\u001B[1;32m","Partner","#9fc700", 5, 0),
    PHASMO_DEV("\u001B[1;33m","Phasmo Dev","#ffff00", 6, 0),
    PHASMO_STAFF("\u001B[1;34m","Phasmo Staff","#00aeff", 7, 0),
    PHASMO_VIP("\u001B[1;33m","Phasmo VIP","#ffc111", 8, 0),
    PHASMO_CC("\u001B[1;35m","Phasmo CC","#ff0000", 9, 0),
    BOT_TEAM("\u001B[1;35m","Shade Team","#ff00cc", 10, 800);

    private final String format;
    private final String name;
    private final String color;
    private final int bitOffset;
    private final int permissionLevel;
    private static final int MAX_BIT_OFFSET = Integer.SIZE - 6;
    private static final int MAX_VALID_OFFSET = values().length;
    private static final String OPEN_CODE = "```ansi\n";
    private static final String CLOSE_CODE = "```";
    Rank(String format, String name, String color, int bitOffset, int permissionLevel){
        this.format = format;
        this.name = name;
        this.color = color;
        this.bitOffset = bitOffset;
        this.permissionLevel = permissionLevel;
    }

    private String getFormatted(){
        return format + "[" + name + "]" + "\u001B[0m";
    }

    public String getName(){
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getBitOffset(){
        return bitOffset;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * Checks if this rank is preset in this user's set of ranks
     * @param user The user
     * @return True if the rank is present
     */
    public boolean isPresent(User user) {
        int flags = UserIo.getRankFlags(user);
        return (getOnlyPresentRanks(flags) & bitOffset) != 0;
    }

    /**
     * Checks if the rank contains at least the permission level of the Rank parameter
     * @param rank The rank whose permission level should be present within the checked rank
     * @return True if the same or higher permission level is present
     */
    public boolean hasPermissionOf(Rank rank) {
        if (rank == null) return true;
        return permissionLevel >= rank.getPermissionLevel();
    }

    /**
     * Checks if the User has any Rank with a higher or equal permission level
     * @param user The user checked
     * @return True if the user has at least the same permission level
     */
    public boolean isPermittedTo(User user) {
        for (Rank rank : getUserRanks(user)) {
            if (rank.getPermissionLevel() >= permissionLevel) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the User has any Rank with a higher or equal permission level
     * @param userId The ID of the user checked
     * @return True if the user has at least the same permission level
     */
    public boolean isPermittedTo(String userId) {
        for (Rank rank : getUserRanks(userId)) {
            if (rank.getPermissionLevel() >= permissionLevel) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Rank with the highest permission present in the user's Ranks
     */
    public static Rank getHighestPermission(String userId) {
        Rank highestRank = null;
        for (Rank rank : getUserRanks(userId)) {
            if (comparePermission(rank, highestRank) >= 0) {
                highestRank = rank;
            }
        }
        return highestRank;
    }

    /**
     * Compares the permission level of two users. Similar to Integer#compare().
     * @param userId1 The ID of the first user to compare
     * @param userId2 The ID of the second user to compare
     * @return 0 if permission levels are the same, <br>
     * a value less than 0 if userId1 has a lower perm level than userId2, <br>
     * a value higher than 0 if userId1 has a higher perm level than userId2
     */
    public static int comparePermission(String userId1, String userId2) {
        Rank rank1 = getHighestPermission(userId1);
        Rank rank2 = getHighestPermission(userId2);
        return comparePermission(rank1, rank2);
    }

    /**
     * Compares the permission level of two ranks. Similar to Integer#compare().
     * @param rank1 The first rank to compare
     * @param rank2 The second rank to compare
     * @return 0 if permission levels are the same, <br>
     * a value less than 0 if rank1 has a lower perm level than rank2, <br>
     * a value higher than 0 if rank1 has a higher perm level than rank2
     */
    public static int comparePermission(Rank rank1, Rank rank2) {
        if (rank1 == rank2) return 0;
        else if (rank1 == null) return -1;
        else if (rank2 == null) return 1;
        else return Integer.compare(rank1.getPermissionLevel(), rank2.getPermissionLevel());
    }

    /**
     * Returns the rank currently set to active for this user.
     * @param user The user
     * @return The active rank or null if there is no active rank
     */
    public static Rank getActiveRank(User user){
        int flags = UserIo.getRankFlags(user);
        return getActiveRankFromFlags(flags);
    }

    /**
     * Returns the rank currently set to active according to the rank flags.
     * @param rankFlags The flags to extract the rank from
     * @return The active rank or null if there is no active rank
     */
    private static Rank getActiveRankFromFlags(int rankFlags){
        if (!isActiveRankPresent(rankFlags)) return null;
        int bitOffset = rankFlags >>> (MAX_BIT_OFFSET);
        return getFromBitOffset(bitOffset);
    }

    private static Rank getFromBitOffset(int bitOffset){
        for (Rank r : Rank.values()){
            if (r.bitOffset == bitOffset) return r;
        }
        return null;
    }

    private static int getOnlyPresentRanks(int rankFlags){
        return (rankFlags << (Integer.SIZE - MAX_BIT_OFFSET)) >>> (Integer.SIZE - MAX_BIT_OFFSET);
    }

    /**
     * Adds the given rank to the user.
     * @param user The user
     * @param rank The rank to add
     */
    public static void addRankToUser(User user, Rank rank){
        addRankToUser(user, rank.bitOffset);
    }

    /**
     * Removes the given rank from the user.
     * @param user The user
     * @param rank The rank to remove
     */
    public static void removeRankFromUser(User user, Rank rank){
        removeRankFromUser(user, rank.bitOffset);
    }

    /**
     * Adds the rank that matches the flag at the given bit offset to a user.
     * @param user The user
     * @param bitOffset The bit offset of the rank
     * @throws IllegalArgumentException if the provided offset is higher than the highest valid flag offset
     */
    public static void addRankToUser(User user, int bitOffset){
        if (bitOffset > MAX_VALID_OFFSET) throw new IllegalArgumentException();
        int rankFlags = UserIo.getRankFlags(user);
        int newRankFlags = rankFlags | (1 << bitOffset);
        UserIo.setRankFlags(user, newRankFlags);
    }

    /**
     * Removes the rank that matches the flag at the given bit offset from a user.
     * @param user The user
     * @param bitOffset The bit offset of the rank
     * @throws IllegalArgumentException if the provided offset is higher than the highest valid flag offset
     */
    public static void removeRankFromUser(User user, int bitOffset){
        if (bitOffset > MAX_VALID_OFFSET) throw new IllegalArgumentException();
        int oldFlags = UserIo.getRankFlags(user);
        int newFlags = oldFlags & ~(1 << bitOffset);
        if (!isActiveRankPresent(newFlags)) newFlags = getOnlyPresentRanks(newFlags);
        UserIo.setRankFlags(user, newFlags);
    }

    /**
     * Sets the next available rank to active using ascending order of the bit offset. <br>
     * If no other rank is available the active rank will not change.
     * @param user The user whose active rank is to set
     * @return The new active rank
     */
    public static Rank setNextActive(User user){
        int flags = UserIo.getRankFlags(user);
        int activeOffset = flags >>> (MAX_BIT_OFFSET);
        int rawFlags = getOnlyPresentRanks(flags);
        int isolatedFlags = rawFlags & ~(1 << activeOffset);
        if (rawFlags == 0) {
            return null; // User has no ranks at all
        } else if (isolatedFlags == 0) {
            UserIo.setRankFlags(user, rawFlags);
            return null; // no other ranks found
        } else {
            int higherThanActiveBits = (isolatedFlags >>> activeOffset) << activeOffset;
            int lowestHigherBit = Integer.lowestOneBit(higherThanActiveBits);
            if (lowestHigherBit == 0) {
                UserIo.setRankFlags(user, rawFlags);
                return null; // no higher rank found -> active rank set to none.
            } else {
                int newActiveOffset = Integer.numberOfTrailingZeros(lowestHigherBit);
                int newFlags = rawFlags | (newActiveOffset << MAX_BIT_OFFSET);
                UserIo.setRankFlags(user, newFlags);
                return getFromBitOffset(newActiveOffset);
            }
        }
    }

    /**
     * Checks if the given rank is preset in this user's set of ranks
     * @param user The user
     * @param rank The rank to check
     * @return True if the rank is present
     */
    public static boolean isPresent(User user, Rank rank){
        int flags = UserIo.getRankFlags(user);
        return (getOnlyPresentRanks(flags) & rank.bitOffset) != 0;
    }

    /**
     * Checks if the flag of the given rank is present in the bitmask of rankFlags.
     * @param rankFlags The value to inspect the bitmask of
     * @param rank The rank to look for
     * @return True if the bit belonging to the rank is set in the bitmask of rankFlags
     */
    public static boolean isPresent(int rankFlags, Rank rank){
        return (getOnlyPresentRanks(rankFlags) & (1 << rank.bitOffset)) != 0;
    }

    private static boolean isActiveRankPresent(int rankFlags){
        int bitOffset = rankFlags >>> (MAX_BIT_OFFSET);
        return (rankFlags & (1 << bitOffset)) != 0;
    }

    /**
     * Checks if the given user has at least one rank
     * @param user The user to check
     * @return True if the user has one or more ranks
     */
    public static boolean isAnyRankPresent(User user){
        int flags = UserIo.getRankFlags(user);
        return (flags << Integer.SIZE - MAX_BIT_OFFSET) >>> Integer.SIZE - MAX_BIT_OFFSET != 0;
    }

    public static List<Rank> getUserRanks(User user) {
        int flags = UserIo.getRankFlags(user);
        return getRanksFromFlags(flags);
    }

    public static List<Rank> getUserRanks(String userId) {
        int flags = UserIo.getRankFlags(userId);
        return getRanksFromFlags(flags);
    }

    private static List<Rank> getRanksFromFlags(int flags) {
        List<Rank> ranks = new ArrayList<>();
        for (Rank rank : Rank.values()) {
            if (isPresent(flags, rank)) {
                ranks.add(rank);
            }
        }
        return ranks;
    }

    private static String getFormatted(Rank rank, String userName, boolean withUserName, boolean withCodeBlock, int maxLength){

        StringBuilder formattedRank = new StringBuilder();
        if (withCodeBlock) formattedRank.append(OPEN_CODE);

        if (rank != null) {
            formattedRank.append(rank.getFormatted());
            if (withUserName) formattedRank.append(" ");
        }

        if (withUserName) {
            formattedRank.append("\u001B[1;37m"); // white bold
            int prefixLength;
            if (rank == null) prefixLength = 0;
            else prefixLength = rank.name.length() + 3;
            if (prefixLength + userName.length() > maxLength){
                formattedRank.append(userName, 0, maxLength - prefixLength - 3);
                formattedRank.append("...");
            } else {
                formattedRank.append(userName);
            }
        }

        formattedRank.append("\u001B[0m");
        if (withCodeBlock) formattedRank.append(CLOSE_CODE);
        return formattedRank.toString();
    }

    public static String getFormatted(Rank rank, String userName, boolean withUserName, boolean withCodeBlock){
        return getFormatted(rank, userName, withUserName, withCodeBlock, Integer.MAX_VALUE);
    }
    private static String getFormatted(User user, String userName, boolean withUserName, boolean withCodeBlock, int maxLength){
        int rankFlags = UserIo.getRankFlags(user);
        Rank activeRank = getActiveRankFromFlags(rankFlags);

        return getFormatted(activeRank, userName, withUserName, withCodeBlock, maxLength);
    }

    public static String getFormatted(User user, boolean withUserName, boolean withCodeBlock, int maxLength){
        return getFormatted(user, user.getName(), withUserName, withCodeBlock, maxLength);
    }

    public static String getFormatted(User user, boolean withUserName, boolean withCodeBlock){
        return getFormatted(user, user.getName(), withUserName,withCodeBlock, Integer.MAX_VALUE);
    }

    public static String getFormatted(Member member, boolean withUserName, boolean withCodeBlock, int maxLength){
        return getFormatted(member.getUser(), member.getEffectiveName(), withUserName, withCodeBlock, maxLength);
    }

    public static String getFormatted(String userId, String userName, boolean withUserName, boolean withCodeBlock, int maxLength) {
        int rankFlags = UserIo.getRankFlags(userId);
        Rank activeRank = getActiveRankFromFlags(rankFlags);
        return getFormatted(activeRank, userName, withUserName, withCodeBlock, maxLength);
    }

    public static String getFormatted(Member member, boolean withUserName, boolean withCodeBlock){
        return getFormatted(member.getUser(), member.getEffectiveName(), withUserName, withCodeBlock, Integer.MAX_VALUE);
    }
}
