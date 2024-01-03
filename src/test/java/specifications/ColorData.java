package specifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorData {

    private Integer id;
    private String name;
    private Integer year;
    private String color;
    private String pantone_value;

}
