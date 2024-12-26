package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.utils.doofutil.BaseLong;
import at.dici.shade.utils.phasmophobia.Evidence;
import at.dici.shade.utils.phasmophobia.Ghost;

import java.util.ArrayList;
import java.util.List;

public class GeaStringifier {

    /*
     *
     * Version 0
     *
     *
     * Bit Mask of serialized GEA:
     *
     *  0000 0000 1111 1111 0000 0000 1111 1111 0000 0000 1111 1111 0000 0000 1111 1111
     * |                 48|  44|  40|                 24|                  8|        0|
     * |        16         |  4 |  4 |        16         |         16        |    8    |
     * |       BLANK       | EQ |  S |  EVIDENCE VALUES  |    EVIDENCE SET   | VERSION |
     *
     * EQ = evidenceQuantity
     * S = speed
     *
     */

    private static final String LOG_PREFIX = "GEA-Stringifier: ";
    private static final long VERSION = 1;
    private static final int VERSION_OFFSET = 0;
    private static final int E_SET_OFFSET = 8;
    private static final int E_VALUES_OFFSET = 24;
    private static final int SPEED_OFFSET = 40;
    private static final int E_QTY_OFFSET = 44;

    private static final int VERSION_LENGTH = 8;
    private static final int E_SET_LENGTH = 16;
    private static final int E_VALUES_LENGTH = 16;
    private static final int SPEED_LENGTH = 4;
    private static final int E_QTY_LENGTH = 4;



    static GEA deserialize(String string) {
        long rawValues;
        if (string.isEmpty()) {
            return new GEA();
        } else {
            try {
                rawValues = BaseLong.parseLong(BaseLong.RADIX_42_NO_VOWELS, string);
            } catch (IllegalArgumentException e) {
                return new GEA();
            }
        }

        long version = (rawValues << (Long.SIZE - VERSION_LENGTH)) >>> (Long.SIZE - VERSION_LENGTH);
        long evidenceSetBits = (rawValues << (Long.SIZE - E_SET_OFFSET - E_SET_LENGTH)) >>> (Long.SIZE - E_SET_LENGTH);
        long evidenceValuesBits = (rawValues << (Long.SIZE - E_VALUES_OFFSET - E_VALUES_LENGTH)) >>> (Long.SIZE - E_VALUES_LENGTH);
        long speedBits = (rawValues << (Long.SIZE - SPEED_OFFSET - SPEED_LENGTH)) >>> (Long.SIZE - SPEED_LENGTH);
        long evidenceQtyBits = (rawValues << (Long.SIZE - E_QTY_OFFSET - E_QTY_LENGTH)) >>> (Long.SIZE - E_QTY_LENGTH);


        List<Evidence> trueEvidences = new ArrayList<>();
        List<Evidence> falseEvidences = new ArrayList<>();

        Ghost.Speed speed;
        int evidenceQuantity = (int) (evidenceQtyBits << Integer.SIZE >>> Integer.SIZE);

        for (Evidence e : Evidence.values()) {

            if (((1L << (e.getId() - 1)) & evidenceSetBits) != 0) {
                if (((1L << (e.getId() - 1)) & evidenceValuesBits) != 0) {
                    trueEvidences.add(e);
                } else {
                    falseEvidences.add(e);
                }
            }
        }

        if (speedBits == 0) speed = null;
        else if (speedBits == 1) speed = Ghost.Speed.NORMAL;
        else if (speedBits == 2) speed = Ghost.Speed.FAST;
        else if (speedBits == 3) speed = Ghost.Speed.SLOW;
        else speed = null;


        return new GEA(trueEvidences, falseEvidences, speed, evidenceQuantity);
    }

    static String serialize(GEA gea) {
        if (gea == null) return "";

        long serializedGea;


        long evidenceSetBits = 0L;
        long evidenceValuesBits = 0L;
        long speedBits = 0L;
        long evidenceQtyBits;

        for (Evidence e : gea.getFalseEvidences()) {
            evidenceSetBits |= 1L << (e.getId() - 1);
        }

        for (Evidence e : gea.getTrueEvidences()) {
            evidenceSetBits |= 1L << (e.getId() - 1);
            evidenceValuesBits |= 1L << (e.getId() - 1);
        }

        if (gea.getSpeed() == Ghost.Speed.NORMAL) speedBits = 1;
        else if (gea.getSpeed() == Ghost.Speed.FAST) speedBits = 2;
        else if (gea.getSpeed() == Ghost.Speed.SLOW) speedBits = 3;

        evidenceQtyBits = gea.getEvidenceQuantity();

        serializedGea =
                (VERSION << VERSION_OFFSET)
                        | (evidenceSetBits << E_SET_OFFSET)
                        | (evidenceValuesBits << E_VALUES_OFFSET)
                        | (speedBits << SPEED_OFFSET)
                        | (evidenceQtyBits << E_QTY_OFFSET);

        String stringifiedGea;

        try {
            stringifiedGea = BaseLong.toString(BaseLong.RADIX_42_NO_VOWELS, serializedGea);
        } catch (IllegalArgumentException exception) {
            stringifiedGea = "";
        }

        return stringifiedGea;
    }
}
