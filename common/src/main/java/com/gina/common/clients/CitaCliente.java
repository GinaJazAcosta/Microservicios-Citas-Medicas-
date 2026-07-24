package com.gina.common.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaCliente {
    @GetMapping("/paciente/{idPaciente}/tiene-citas-activas")
    Boolean tieneCitasActivasPaciente(@PathVariable Long idPaciente);

    @GetMapping("/medico/{idMedico}/tiene-citas-activas")
    Boolean tieneCitasActivasMedico(@PathVariable Long idMedico);
}
