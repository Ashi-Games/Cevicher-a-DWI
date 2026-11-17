// La API se busca en el mismo equipo que sirve la pagina (PC o IP de la red WiFi),
// asi funciona igual en el navegador de la PC y en el celular.
export const environment = {
  apiUrl: `${location.protocol}//${location.hostname}:8080/api`,
};
