package at.dici.shade.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserUtil {
    private String id;
    private String datum;
    private String permission;
    private int rank;
    private int points;
    private int dailyc;

}
