package com.app.preguntasrespuestas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.preguntasrespuestas.models.ProyectosGamificacion;

@FeignClient(name = "app-gamification")
public interface GamificacionFeignClient {

	@PutMapping("/gamificacion/proyectos/agregar-participante/{nombre}")
	public Boolean agregarParticipante(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username);

	@GetMapping("/gamificacion/proyectos/ver/{nombre}")
	public ProyectosGamificacion verGamificacionProyectos(@PathVariable("nombre") String nombre);

	@GetMapping("/gamificacion/proyectos/ver-habilitado/{nombre}")
	public Boolean verHabilitadoProyecto(@PathVariable("nombre") String nombre);
}
