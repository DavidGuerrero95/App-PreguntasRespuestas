package com.app.preguntasrespuestas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.preguntasrespuestas.models.PreguntasRespuestas;

@FeignClient("app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadistica/preguntasrespuestas/crear/")
	public Boolean crearPreguntasRespuestas(@RequestBody PreguntasRespuestas pr);

	@PutMapping("/estadistica/preguntasrespuestas/editar/")
	public Boolean editarPreguntasRespuestas(@RequestBody PreguntasRespuestas pr);

	@PutMapping("/estadistica/obtenerEstadistica/{nombre}")
	public Boolean obtenerEstadistica(@PathVariable("nombre") String nombre);

}
