package com.app.preguntasrespuestas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-subscripciones")
public interface SuscripcionesFeignClient {

	@PutMapping("/suscripciones/cuestionario/inscribir/{nombre}")
	public Boolean inscribirCuestionario(@PathVariable String nombre, @RequestParam String usuario);

}
