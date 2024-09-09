
package io.project.gate.models;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author armen
 */
@Data
public class SpecialResponse implements Serializable{
    
    private String code;
    private String message;
    private String data;

    public SpecialResponse(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    
    
    
}
