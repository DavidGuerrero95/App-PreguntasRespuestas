package com.app.preguntasrespuestas.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.preguntasrespuestas.clients.EstadisticaFeignClient;
import com.app.preguntasrespuestas.clients.SuscripcionesFeignClient;
import com.app.preguntasrespuestas.models.PreguntasRespuestas;
import com.app.preguntasrespuestas.repository.PreguntasRespuestasRepository;
import com.app.preguntasrespuestas.request.Preguntas;
import com.app.preguntasrespuestas.request.Respuestas;

@RestController
public class PreguntasRespuestasController {

	private final Logger logger = LoggerFactory.getLogger(PreguntasRespuestasController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	PreguntasRespuestasRepository prRepository;

	@Autowired
	EstadisticaFeignClient eClient;

	@Autowired
	SuscripcionesFeignClient sClient;

	// Crear Cuestionario
	@PostMapping("/preguntasrespuestas/cuestionario/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearCuestionario(@RequestParam("nombre") String nombre) throws IOException {
		try {
			PreguntasRespuestas pr = new PreguntasRespuestas();
			pr.setNombre(nombre);
			pr.setPreguntas(new ArrayList<Preguntas>());
			pr.setRespuestas(new ArrayList<List<List<String>>>());
			pr.setUsuarios(new ArrayList<String>());
			pr.setRespuestasUsuario(new ArrayList<List<List<String>>>());
			prRepository.save(pr);
			if (cbFactory.create("preguntasrespuestas").run(() -> eClient.crearPreguntasRespuestas(pr),
					e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			return true;
		} catch (Exception e2) {
			throw new IOException("error crear cuestionario, preguntasrespuestas: " + e2.getMessage());
		}

	}

	// Usuario Abre Cuestionario
	@PutMapping("/preguntasrespuestas/cuestionario/usuario/abrir/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void usuarioAbreCuestionario(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username) {
		PreguntasRespuestas pr = prRepository.findByNombre(nombre);
		List<String> personas = pr.getUsuarios();
		if (!personas.contains(username)) {
			personas.add(username);
			List<List<List<String>>> listaRespuestas = pr.getRespuestasUsuario();
			for (int i = 0; i < listaRespuestas.size(); i++) {
				listaRespuestas.get(i).add(new ArrayList<String>());
			}
			pr.setUsuarios(personas);
			pr.setRespuestasUsuario(listaRespuestas);
			if (cbFactory.create("usuario").run(() -> eClient.editarPreguntasRespuestas(pr), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			prRepository.save(pr);
		}
	}

	@PutMapping("/preguntasrespuestas/preguntas/crear/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editarPreguntas(@PathVariable("nombre") String nombre,
			@RequestBody @Validated Preguntas pregunta) {
		if (prRepository.existsByNombre(nombre)) {
			List<List<String>> respuesta = new ArrayList<List<String>>();
			PreguntasRespuestas pr = prRepository.findByNombre(nombre);
			List<Preguntas> listaPreguntas = pr.getPreguntas();
			List<List<List<String>>> listRespuestas = pr.getRespuestas();
			List<List<List<String>>> listaRespuestasUsuarios = pr.getRespuestasUsuario();
			pregunta.setNumeroPregunta(pr.getPreguntas().size() + 1);
			if (pregunta.getPriorizacion() == null) {
				pregunta.setPriorizacion("priorizacion");
			}
			if (pregunta.getTipoConsulta() == 5) {
				pregunta.setPriorizacion("");
				pregunta.setOpciones(new ArrayList<String>());
			}
			listaPreguntas.add(pregunta);
			listRespuestas.add(respuesta);
			listaRespuestasUsuarios.add(respuesta);
			pr.setRespuestas(listRespuestas);
			pr.setRespuestasUsuario(listaRespuestasUsuarios);
			pr.setPreguntas(listaPreguntas);
			if (cbFactory.create("usuario").run(() -> eClient.editarPreguntasRespuestas(pr), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			prRepository.save(pr);
			return ResponseEntity.ok("Pregunta almacenada correctamente en el proyecto: " + nombre);
		}
		return ResponseEntity.badRequest().body("Proyecto: " + nombre + " no existe");
	}

	@PutMapping("/preguntasrespuestas/respuestas/colocar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> colocarRespuesta(@PathVariable("nombre") String nombre,
			@RequestBody Respuestas respuesta) {
		try {
			PreguntasRespuestas pr = prRepository.findByNombre(nombre);
			List<List<List<String>>> listaRespuestas = pr.getRespuestas();

			for (int i = 0; i < listaRespuestas.size(); i++) {
				listaRespuestas.get(i).add(respuesta.getRespuesta().get(i));
			}
			pr.setRespuestas(listaRespuestas);
			if (cbFactory.create("usuario").run(() -> eClient.editarPreguntasRespuestas(pr), e -> errorConexion(e))) {
				logger.info("Creacion Estadisticas Correcta");
			}
			prRepository.save(pr);
			if (cbFactory.create("usuario").run(() -> sClient.inscribirCuestionario(nombre, respuesta.getUsername()),
					e -> errorConexion(e))) {
				logger.info("Creacion Suscripciones Correcta");
			}
			if (cbFactory.create("usuario").run(() -> eClient.obtenerEstadistica(nombre), e -> errorConexion(e))) {
				logger.info("Obtener Estadistica Correcta");
			}

			return ResponseEntity.ok("Respuesta añadida");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
					"Proyecto: " + nombre + " no existe, error: " + e.getMessage() + ":" + e.getLocalizedMessage());
		}

	}

	@GetMapping("/preguntasrespuestas/pregunta/respuesta/obtener/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public PreguntasRespuestas getProyectosByNombre(@PathVariable("nombre") String nombre) throws IOException {
		try {
			return prRepository.findByNombre(nombre);
		} catch (Exception e) {
			throw new IOException("error obtener respuesta, preguntasrespuestas: " + e.getMessage());
		}

	}

	@GetMapping("/preguntasrespuestas/preguntas/ver/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Preguntas> verPreguntas(@PathVariable("nombre") String nombre) {
		PreguntasRespuestas proyecto = prRepository.findByNombre(nombre);
		if (proyecto.getPreguntas().size() != 0) {
			return proyecto.getPreguntas();
		} else {
			return null;
		}

	}

	@GetMapping("/preguntasrespuestas/respuestas/ver/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<List<String>>> verRespuestas(@PathVariable("nombre") String nombre) {
		PreguntasRespuestas proyecto = prRepository.findByNombre(nombre);
		return proyecto.getRespuestas();
	}

	@DeleteMapping("/preguntasrespuestas/preguntas/borrar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean borrarPreguntas(@PathVariable("nombre") String nombre) throws IOException {
		try {
			PreguntasRespuestas proyecto = prRepository.findByNombre(nombre);
			prRepository.delete(proyecto);
			return true;
		} catch (Exception e) {
			throw new IOException("error borrar pregunta, preguntasrespuestas: " + e.getMessage());
		}

	}

	@PutMapping("/preguntasrespuestas/cuestionario/responder/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void respuestasPregunta(@PathVariable("nombre") String nombre, @RequestBody Respuestas r) {
		List<String> respuesta = new ArrayList<String>();
		PreguntasRespuestas pr = prRepository.findByNombre(nombre);
		List<Preguntas> listaPreguntas = pr.getPreguntas();
		Integer valueUser = pr.getUsuarios().indexOf(r.getUsername());
		List<List<List<String>>> listaRespuestasUsuario = pr.getRespuestasUsuario();
		List<List<String>> respuestaUsuario = listaRespuestasUsuario.get(r.getNumeroPregunta() - 1);
		List<String> opcionesUsuario = r.getRespuestaUsuario();
		List<String> opciones = listaPreguntas.get(r.getNumeroPregunta() - 1).getOpciones();
		if (listaPreguntas.get(r.getNumeroPregunta() - 1).getTipoConsulta() == 1) {
			Collections.reverse(opcionesUsuario);
			Double value = 100.0;
			if (opciones.size() != 0)
				value = 100.0 / opciones.size();
			for (int i = 0; i < opciones.size(); i++) {
				Double index = (double) (opcionesUsuario.indexOf(opciones.get(i)) + 1);
				respuesta.add(String.valueOf(value * index));
			}
			respuestaUsuario.set(valueUser, respuesta);
		} else {
			respuestaUsuario.set(valueUser, r.getRespuestaUsuario());
		}

		listaRespuestasUsuario.set(r.getNumeroPregunta() - 1, respuestaUsuario);
		pr.setRespuestasUsuario(listaRespuestasUsuario);
		if (cbFactory.create("usuario").run(() -> eClient.editarPreguntasRespuestas(pr), e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
		prRepository.save(pr);
	}

	@PutMapping("/preguntasrespuestas/respuestaFinal/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> respuestaFinal(@PathVariable("nombre") String nombre, @RequestBody Respuestas respuesta) {
		if (prRepository.existsByNombre(nombre)) {
			PreguntasRespuestas proyecto = prRepository.findByNombre(nombre);
			List<List<List<String>>> listaRespuestas = proyecto.getRespuestas();
			List<List<List<String>>> listaRespuestasUsuario = proyecto.getRespuestasUsuario();
			Integer index = proyecto.getUsuarios().indexOf(respuesta.getUsername());
			for (int i = 0; i < listaRespuestasUsuario.size(); i++) {
				if (listaRespuestasUsuario.get(i).get(index).size() == 0) {
					return ResponseEntity.badRequest().body("LLenar toddas las opciones");
				} else {
					listaRespuestas.get(i).add(listaRespuestasUsuario.get(i).get(index));
				}
			}

			List<String> usuarios = proyecto.getUsuarios();
			for (int i = 0; i < listaRespuestasUsuario.size(); i++) {
				List<List<String>> res = listaRespuestasUsuario.get(i);
				res.remove(proyecto.getUsuarios().indexOf(respuesta.getUsername()));
				listaRespuestasUsuario.set(i, res);
			}
			usuarios.remove(respuesta.getUsername());
			proyecto.setRespuestas(listaRespuestas);
			proyecto.setRespuestasUsuario(listaRespuestasUsuario);
			prRepository.save(proyecto);
			if (cbFactory.create("usuario").run(() -> sClient.inscribirCuestionario(nombre, respuesta.getUsername()),
					e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("usuario").run(() -> eClient.obtenerEstadistica(nombre), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			return ResponseEntity.ok("Respuesta añadida");
		}
		return ResponseEntity.badRequest().body("Proyecto: " + nombre + " no existe");

	}

	public Boolean errorConexion(Throwable e) {
		logger.info(e.getMessage());
		return false;
	}

}
