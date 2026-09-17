package navegador.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catálogo de sitios simulados. Todo es local: no se realiza ninguna
 * petición de red, únicamente se busca la URL en este mapa.
 */
public class CatalogoLocal {

    private final Map<String, Pagina> sitios = new LinkedHashMap<>();

    public CatalogoLocal() {
        registrar("https://www.google.com", "Google", "Sitio oficial de Google");
        registrar("https://www.mozilla.org", "Mozilla", "Bienvenido a Mozilla");
        registrar("https://openai.com", "OpenAI", "Sitio oficial de OpenAI");
        registrar("https://stackoverflow.com", "Stack Overflow", "Bienvenido a Stack Overflow");
        registrar("https://github.com", "GitHub", "Sitio oficial de GitHub");
        registrar("https://merida.anahuac.mx", "Universidad Anáhuac Mayab",
                "Bienvenido a la Universidad Anáhuac Mayab");
        registrar("https://www.wikipedia.org", "Wikipedia", "La enciclopedia libre");
        registrar("https://www.youtube.com", "YouTube", "Videos y transmisiones en vivo");
        registrar("https://www.java.com", "Java", "Todo sobre la plataforma Java");
    }

    private void registrar(String url, String titulo, String descripcion) {
        sitios.put(clave(url), new Pagina(url, titulo, descripcion));
    }

    /**
     * Busca la URL en el catálogo. Si no existe devuelve una página 404
     * (así el navegador nunca falla, tal como sucede en un navegador real).
     */
    public Pagina abrir(String urlNormalizada) {
        Pagina pagina = sitios.get(clave(urlNormalizada));
        return pagina != null ? pagina : Pagina.noEncontrada(urlNormalizada);
    }

    /** URLs disponibles, para mostrarlas como sugerencias en la interfaz. */
    public List<String> urlsDisponibles() {
        List<String> urls = new ArrayList<>();
        for (Pagina pagina : sitios.values()) {
            urls.add(pagina.getUrl());
        }
        return urls;
    }

    /** Normaliza el texto escrito por el usuario para convertirlo en una URL. */
    public static String normalizar(String textoUsuario) {
        String url = textoUsuario == null ? "" : textoUsuario.trim();
        if (url.isEmpty()) {
            return "";
        }
        if (!url.contains("://")) {
            url = "https://" + url;
        }
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static String clave(String url) {
        String clave = normalizar(url).toLowerCase();
        return clave.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
    }
}
