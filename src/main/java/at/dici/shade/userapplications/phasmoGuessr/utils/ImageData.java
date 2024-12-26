package at.dici.shade.userapplications.phasmoGuessr.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ImageData implements Serializable {
    public String url;
    public int phasmoMapId;
    public String room;
    public int imageindex;

}