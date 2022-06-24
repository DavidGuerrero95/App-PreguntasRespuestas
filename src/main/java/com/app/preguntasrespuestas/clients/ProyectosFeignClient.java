package com.app.preguntasrespuestas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.app.preguntasrespuestas.models.Proyectos;

@FeignClient(name = "app-proyectos")
public interface ProyectosFeignClient {

	@GetMapping("/proyectos/ver/proyecto/{nombre}")
	public Proyectos verProyecto(@PathVariable("nombre") String nombre);

	@GetMapping("/proyectos/gamificacion/ver-estado/{nombre}")
	public Boolean verEstadoGamificacion(@PathVariable("nombre") String nombre);
}
