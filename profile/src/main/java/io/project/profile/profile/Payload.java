package io.project.profile.profile;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author armena
 */
@Data
@AllArgsConstructor
public class Payload implements Serializable{

    private String name;
    private String email;
}
