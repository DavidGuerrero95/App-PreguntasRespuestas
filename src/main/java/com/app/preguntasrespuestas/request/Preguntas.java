package com.app.preguntasrespuestas.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Preguntas {

	private Integer numeroPregunta;

	@NotNull(message = "Tipo Consulta cannot be null")
	@Max(6)
	@Min(1)
	private Integer tipoConsulta;

	@NotBlank(message = "Pregunta cannot be null")
	private String pregunta;

	private String informacion;
	private List<String> opciones;

	@NotNull(message = "Obligatorio cannot be null")
	private Boolean obligatorio;

	private String priorizacion;

	public Preguntas() {
	}

	public Preguntas(Integer numeroPregunta, Integer tipoConsulta, String pregunta, String informacion,
			List<String> opciones, Boolean obligatorio, String priorizacion) {
		super();
		this.numeroPregunta = numeroPregunta;
		this.tipoConsulta = tipoConsulta;
		this.pregunta = pregunta;
		this.informacion = informacion;
		this.opciones = opciones;
		this.obligatorio = obligatorio;
		this.priorizacion = priorizacion;
	}

	public Integer getNumeroPregunta() {
		return numeroPregunta;
	}

	public void setNumeroPregunta(Integer numeroPregunta) {
		this.numeroPregunta = numeroPregunta;
	}

	public Integer getTipoConsulta() {
		return tipoConsulta;
	}

	public void setTipoConsulta(Integer tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public String getInformacion() {
		return informacion;
	}

	public void setInformacion(String informacion) {
		this.informacion = informacion;
	}

	public List<String> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<String> opciones) {
		this.opciones = opciones;
	}

	public Boolean getObligatorio() {
		return obligatorio;
	}

	public void setObligatorio(Boolean obligatorio) {
		this.obligatorio = obligatorio;
	}

	public String getPriorizacion() {
		return priorizacion;
	}

	public void setPriorizacion(String priorizacion) {
		this.priorizacion = priorizacion;
	}

}
