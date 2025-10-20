package cl.previred.personas.viewmodel;

import java.io.Serializable;

public class Wrapper implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1764598016620660340L;
    private int httpCode;
    private String descripcion;
    private Object data;

    public Wrapper() {
        super();
    }

    public Wrapper(Object data, String descripcion, int httpCode) {
        super();
        this.data = data;
        this.descripcion = descripcion;
        this.httpCode = httpCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

}
