package com.enriquealberto.model;

import java.util.*;

import com.enriquealberto.Lectores.LectorHeroes;
import com.enriquealberto.Lectores.LectorMostruo;
import com.enriquealberto.interfaces.Observer;

/**
 * Clase principal que gestiona la lógica del juego, implementando el patrón Singleton.
 * Controla el estado del juego, los personajes, los turnos y las interacciones entre ellos.
 */
public class Juego {
    private static Juego instance;
    private ArrayList<Observer> observers;
    private ArrayList<Heroe> heroes;
    private ArrayList<Enemigo> enemigos;
    private String nombre;
    private int turnoIndex = 0;

    private GestorMapas gestorMapas;

    private ArrayList<Personaje> entidades;
    private Map<Posicion, Personaje> entidadesMapa = new HashMap<>();
    private int dificultad;
    private Heroe jugador;
    private ArrayList<Enemigo> enemigosAutista = new ArrayList<>();
    private ArrayList<Enemigo> enemigosF = new ArrayList<>(); // Enemigos tipo F (fáciles)
    private ArrayList<Enemigo> enemigosM = new ArrayList<>(); // Enemigos tipo M (medios)
    private ArrayList<Enemigo> enemigosD = new ArrayList<>(); // Enemigos tipo D (difíciles)
    private Mapa mapaActual;
    private int[][] MatrizMapa;

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa los componentes principales del juego.
     */
    public Juego() {
        this.observers = new ArrayList<>();
        this.heroes = LectorHeroes.leerHeroes();
        this.enemigos = LectorMostruo.leerMostruo();
        clasificarEnemigos();
        this.jugador = null;
        this.gestorMapas = new GestorMapas();
        this.mapaActual = gestorMapas.getMapaActual();
        this.MatrizMapa = mapaActual.getMapa();
        this.entidades = new ArrayList<>();
    }

    /**
     * Obtiene la instancia única del juego (Singleton).
     * @return La instancia única del juego
     */
    public static Juego getInstance() {
        if (instance == null) {
            instance = new Juego();
        }
        return instance;
    }

    /**
     * Establece la instancia del juego (útil para testing).
     * @param instance La instancia del juego a establecer
     */
    public static void setInstance(Juego instance) {
        Juego.instance = instance;
    }

    /**
     * Suscribe un observador para recibir notificaciones de cambios en el juego.
     * @param observer El observador a suscribir
     */
    public void suscribe(Observer observer) {
        observers.add(observer);
    }

    /**
     * Elimina un observador de la lista de suscriptores.
     * @param observer El observador a eliminar
     */
    public void unsuscribe(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores sobre cambios en el estado del juego.
     */
    public void notifyObservers() {
        observers.forEach(x -> x.onChange());
    }

    /**
     * Establece el jugador actual y notifica a los observadores.
     * @param jugador El héroe que será controlado por el jugador
     */
    public void setJugador(Heroe jugador) {
        this.jugador = jugador;
        notifyObservers();
    }

    // Getters y Setters básicos con documentación

    /**
     * Obtiene la lista de héroes disponibles en el juego.
     * @return Lista de héroes
     */
    public ArrayList<Heroe> getHeroes() {
        return heroes;
    }

    /**
     * Establece la lista de héroes del juego.
     * @param heroes Lista de héroes a establecer
     */
    public void setHeroes(ArrayList<Heroe> heroes) {
        this.heroes = heroes;
    }

    /**
     * Obtiene la lista de enemigos del juego.
     * @return Lista de enemigos
     */
    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    /**
     * Establece la lista de enemigos del juego.
     * @param enemigos Lista de enemigos a establecer
     */
    public void setEnemigos(ArrayList<Enemigo> enemigos) {
        this.enemigos = enemigos;
    }

    /**
     * Obtiene el héroe controlado por el jugador.
     * @return El héroe jugador
     */
    public Heroe getJugador() {
        return jugador;
    }

    /**
     * Obtiene el nivel de dificultad actual del juego.
     * @return Nivel de dificultad (1-3)
     */
    public int getDificultad() {
        return dificultad;
    }

    /**
     * Establece el nivel de dificultad del juego y notifica a los observadores.
     * @param dificultad Nivel de dificultad (1-3)
     */
    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
        notifyObservers();
    }

    /**
     * Obtiene el nombre del juego.
     * @return Nombre del juego
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del juego y notifica a los observadores.
     * @param nombre Nombre del juego
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
        notifyObservers();
    }

    /**
     * Clasifica los enemigos en tres categorías según su tipo (F, M, D).
     */
    public void clasificarEnemigos() {
        for (Enemigo enemigo : enemigos) {
            switch (enemigo.getT_enemigo()) {
                case 1:
                    enemigosF.add(enemigo);
                    break;
                case 2:
                    enemigosM.add(enemigo);
                    break;
                case 3:
                    enemigosD.add(enemigo);
                    break;
                case 4:
                    enemigosAutista.add(enemigo);
                    break;
            }
        }
    }

    /**
     * Inicializa las entidades del juego (jugador y enemigos) en el mapa actual.
     * Coloca al jugador en la posición (0,0) y distribuye los enemigos aleatoriamente.
     */
    public void iniciarentidades() {
        Random random = new Random();
        entidades.clear();
        entidadesMapa.clear();
        jugador.setPosicion(new Posicion(0, 0));
        entidades.add(jugador);
        entidadesMapa.put(jugador.getPosicion(), jugador);

        // Calcula la cantidad de enemigos según el nivel y dificultad
        int cantidadF = (int) Math.ceil((2.0 + mapaActual.getNivel()) / 2.0) + (dificultad - 1);
        int cantidadM = (int) (mapaActual.getNivel() / 2.0) + dificultad;
        int cantidadD = (int) (mapaActual.getNivel() / 2.0) + (dificultad - 2);

        // Coloca enemigos fáciles
        for (int i = 0; i < cantidadF; i++) {
            Enemigo copia = enemigosF.get(random.nextInt(enemigosF.size())).clone();
            colocarEnemigoEnPosicionAleatoria(copia);
            entidades.add(copia);
        }

        // Coloca enemigos medios
        for (int i = 0; i < cantidadM; i++) {
            Enemigo copia = enemigosM.get(random.nextInt(enemigosM.size())).clone();
            colocarEnemigoEnPosicionAleatoria(copia);
            entidades.add(copia);
        }

        // Coloca enemigos difíciles
        for (int i = 0; i < cantidadD; i++) {
            Enemigo copia = enemigosD.get(random.nextInt(enemigosD.size())).clone();
            colocarEnemigoEnPosicionAleatoria(copia);
            entidades.add(copia);
        }

        Enemigo copia = enemigosAutista.get(random.nextInt(enemigosAutista.size())).clone();
        colocarEnemigoEnPosicionAleatoria(copia);
        entidades.add(copia);


        Collections.sort(entidades);
    }

    /**
     * Coloca un enemigo en una posición aleatoria válida del mapa.
     * @param enemigo El enemigo a colocar en el mapa
     */
    public void colocarEnemigoEnPosicionAleatoria(Enemigo enemigo) {
        Random random = new Random();
        int x, y;
        do {
            y = random.nextInt(MatrizMapa.length);
            x = random.nextInt(MatrizMapa[0].length);
        } while (comprobarposicion(x, y) != 2); // Busca hasta encontrar una posición vacía
        enemigo.setPosicion(new Posicion(x, y));
        entidadesMapa.put(enemigo.getPosicion(), enemigo);
    }

    // Métodos de movimiento

    /**
     * Intenta mover un personaje a la derecha.
     * @param p Personaje a mover
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean moverDerecha(Personaje p) {
        return mover(p, 1, 0);
    }

    /**
     * Intenta mover un personaje a la izquierda.
     * @param p Personaje a mover
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean moverIzquierda(Personaje p) {
        return mover(p, -1, 0);
    }

    /**
     * Intenta mover un personaje hacia arriba.
     * @param p Personaje a mover
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean moverArriba(Personaje p) {
        return mover(p, 0, -1);
    }

    /**
     * Intenta mover un personaje hacia abajo.
     * @param p Personaje a mover
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean moverAbajo(Personaje p) {
        return mover(p, 0, 1);
    }

    /**
     * Comprueba el estado de una posición en el mapa.
     * @param x Coordenada x
     * @param y Coordenada y
     * @return 0 si la posición es inválida (fuera de límites o obstáculo),
     *         1 si hay un personaje en esa posición,
     *         2 si la posición está vacía y es accesible
     */
    public int comprobarposicion(int x, int y) {
        if (x < 0 || x >= MatrizMapa[0].length || y < 0 || y >= MatrizMapa.length) {
            return 0; // Fuera de los límites del mapa
        }
        if (MatrizMapa[y][x] == 1) {
            return 0; // Obstáculo (pared)
        }
        if (entidadesMapa.containsKey(new Posicion(x, y))) {
            return 1; // Personaje en esa posición
        }
        if (MatrizMapa[y][x] == 0) {
            return 2; // Posición vacía y accesible
        } else {
            return 0; // Otros casos (por defecto, posición inválida)
        }
    }

    /**
     * Método principal para mover un personaje en una dirección específica.
     * Maneja colisiones, ataques y efectos especiales según el tipo de mapa.
     * @param p Personaje a mover
     * @param x Desplazamiento en el eje x
     * @param y Desplazamiento en el eje y
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean mover(Personaje p, int x, int y) {
        Posicion antigua = p.getPosicion();
        int xNueva = antigua.getX() + x;
        int yNueva = antigua.getY() + y;
        int resultado = comprobarposicion(xNueva, yNueva);

        switch (resultado) {
            case 0: // Colisión con obstáculo o límite del mapa
                System.out.println("Colisión de personaje: " + p.getNombre());

                // Efectos especiales en niveles 3 (agua) y 5 (lava)
                if (p instanceof Heroe) {
                    Heroe heroe = (Heroe) p;

                    if (mapaActual.getNivel() == 3 || mapaActual.getNivel() == 5) {
                        if (xNueva >= 0 && xNueva < MatrizMapa[0].length && yNueva >= 0 && yNueva < MatrizMapa.length) {
                            if (MatrizMapa[yNueva][xNueva] == 1) {
                                int vidaPerdida = (mapaActual.getNivel() == 3) ? 1 : 2;
                                heroe.setVida(heroe.getVida() - vidaPerdida);

                                if (mapaActual.getNivel() == 3) {
                                    System.out.println(
                                            "El héroe " + heroe.getNombre() + " se ha caído al agua y ha perdido "
                                                    + vidaPerdida + " de vida. Su vida es " + heroe.getVida());
                                } else if (mapaActual.getNivel() == 5) {
                                    System.out.println(
                                            "El héroe " + heroe.getNombre() + " ha caído a la lava y ha perdido "
                                                    + vidaPerdida + " de vida. Su vida es " + heroe.getVida());
                                }
                            }
                        }
                    }

                    // Verificar si el héroe ha sido derrotado
                    if (heroe.getVida() <= 0) {
                        System.out.println("El héroe " + heroe.getNombre() + " ha sido derrotado.");
                        entidadesMapa.remove(antigua);
                        entidades.remove(heroe);
                        if (verificarDerrota()) {
                            notifyObservers();
                        }
                    }
                }
                return false;

            case 1: // Colisión con otro personaje (ataque)
                Posicion posicionEnemigo = new Posicion(xNueva, yNueva);
                Personaje enemigo = entidadesMapa.get(posicionEnemigo);

                if (enemigo instanceof Enemigo && p instanceof Heroe) {
                    Heroe heroe = (Heroe) p;
                    Enemigo enemigoAtacado = (Enemigo) enemigo;

                    heroe.atacar(heroe, enemigoAtacado);

                    if (enemigoAtacado.getVida() <= 0) {
                        System.out.println("El enemigo " + enemigoAtacado.getNombre() + " ha sido derrotado.");
                        entidadesMapa.remove(posicionEnemigo);
                        entidades.remove(enemigoAtacado);
                        if (verificarVictoria()) {
                            ganarVida(heroe);
                            notifyObservers();
                        } else {
                            ganarVida(heroe);
                        }
                    }
                }
                return true;

            case 2: // Movimiento válido
                Posicion nueva = new Posicion(xNueva, yNueva);
                p.setPosicion(nueva);
                entidadesMapa.remove(antigua);
                entidadesMapa.put(nueva, p);
                return true;

            default:
                return false;
        }
    }

    /**
     * Incrementa la vida del personaje (héroe) si es posible.
     * @param p Personaje al que se le incrementará la vida
     */
    public void ganarVida(Personaje p) {
        if (p instanceof Heroe) {
            Heroe heroe = (Heroe) p;
            if (heroe.getVida() < 10) {
                heroe.setVida(heroe.getVida() + 1);
                System.out.println("El Héroe ha ganado uno de vida. Su vida es " + heroe.getVida());
            } else {
                System.out.println("El Héroe no puede ganar más vida. Su vida es " + heroe.getVida());
            }
        }
    }

    /**
     * Mueve un personaje en una dirección aleatoria hasta que el movimiento sea válido.
     * @param p Personaje a mover
     */
    public void moverAleatorio(Personaje p) {
        Random rand = new Random();
        boolean movido = false;
        do {
            int numero = rand.nextInt(4);
            switch (numero) {
                case 0:
                    movido = moverAbajo(p);
                    break;
                case 1:
                    movido = moverArriba(p);
                    break;
                case 2:
                    movido = moverDerecha(p);
                    break;
                case 3:
                    movido = moverIzquierda(p);
                    break;
            }
        } while (!movido);
    }

    /**
     * Mueve un personaje (enemigo) hacia el jugador de manera inteligente.
     * @param p Personaje a mover (normalmente un enemigo)
     */
    public void moverGuiado(Personaje p) {
        Posicion posActual = p.getPosicion();
        Posicion posJugador = jugador.getPosicion();

        int dx = posJugador.getX() - posActual.getX();
        int dy = posJugador.getY() - posActual.getY();
        boolean movido = false;

        Enemigo enemigo = (Enemigo) p;
        if (enemigo.getT_enemigo() == 4) {
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    movido = moverIzquierda(p);
                    if (!movido)
                        movido = dy > 0 ? moverArriba(p) : moverAbajo(p);
                } else {
                    movido = moverDerecha(p);
                    if (!movido)
                        movido = dy > 0 ? moverArriba(p) : moverAbajo(p);
                }
            } else {
                if (dy > 0) {
                    movido = moverArriba(p);
                    if (!movido)
                        movido = dx > 0 ? moverIzquierda(p) : moverDerecha(p);
                } else {
                    movido = moverAbajo(p);
                    if (!movido)
                        movido = dx > 0 ? moverIzquierda(p) : moverDerecha(p);
                }
            }
        }else{
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    movido = moverDerecha(p);
                    if (!movido)
                        movido = dy > 0 ? moverAbajo(p) : moverArriba(p);
                } else {
                    movido = moverIzquierda(p);
                    if (!movido)
                        movido = dy > 0 ? moverAbajo(p) : moverArriba(p);
                }
            } else {
                if (dy > 0) {
                    movido = moverAbajo(p);
                    if (!movido)
                        movido = dx > 0 ? moverDerecha(p) : moverIzquierda(p);
                } else {
                    movido = moverArriba(p);
                    if (!movido)
                        movido = dx > 0 ? moverDerecha(p) : moverIzquierda(p);
                }
            }
        }
        // Prioriza el movimiento en la dirección con mayor diferencia


        // Si no pudo moverse hacia el jugador, intenta un movimiento aleatorio
        if (!movido)
            moverAleatorio(p);
    }

    /**
     * Maneja el movimiento de un enemigo, incluyendo detección y ataque al jugador.
     * @param e Enemigo que se está moviendo
     */
    public void moverenemigo(Enemigo e) {
        Posicion posActual = e.getPosicion();
        Posicion posJugador = jugador.getPosicion();

        int dx = posJugador.getX() - posActual.getX();
        int dy = posJugador.getY() - posActual.getY();

        // Si está adyacente al jugador, ataca
        if ((Math.abs(dx) == 1 && Math.abs(dy) == 0) || (Math.abs(dx) == 0 && Math.abs(dy) == 1)) {
            e.atacar(jugador, e);
            if (jugador.getVida() <= 0) {
                System.out.println("El heroe " + jugador.getNombre() + " ha sido derrotado.");
                entidadesMapa.remove(posJugador);
                entidades.remove(jugador);
                if (verificarDerrota()) {
                    notifyObservers();
                }
            }
        } else {
            // Si el jugador está dentro del rango de percepción, se mueve hacia él
            if (Math.abs(dx) <= e.getPercepcion() && Math.abs(dy) <= e.getPercepcion()) {
                moverGuiado(e);
            } else {
                // Movimiento aleatorio si no detecta al jugador
                moverAleatorio(e);
            }
        }
    }

    /**
     * Verifica si se han derrotado todos los enemigos (condición de victoria).
     * @return true si no quedan enemigos, false en caso contrario
     */
    public boolean verificarVictoria() {
        for (Personaje p : entidadesMapa.values()) {
            if (p instanceof Enemigo) {
                return false;
            }
        }

        // Avanza al siguiente mapa si es posible
        boolean haySiguienteMapa = gestorMapas.avanzarAlSiguienteMapa();
        if (haySiguienteMapa) {
            System.out.println("¡Victoria! Cambiando al siguiente mapa...");
            mapaActual = gestorMapas.getMapaActual();
            MatrizMapa = mapaActual.getMapa();
            iniciarentidades();
        } else {
            System.out.println("¡Has completado todos los mapas! Fin del juego.");
        }

        return true;
    }

    /**
     * Verifica si el jugador ha sido derrotado (condición de derrota).
     * @return true si no quedan héroes vivos, false en caso contrario
     */
    public boolean verificarDerrota() {
        for (Personaje p : entidadesMapa.values()) {
            if (p instanceof Heroe) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene el gestor de mapas del juego.
     * @return El gestor de mapas
     */
    public GestorMapas getGestorMapas() {
        return gestorMapas;
    }

    /**
     * Establece el gestor de mapas del juego.
     * @param gestorMapas Gestor de mapas a establecer
     */
    public void setGestorMapas(GestorMapas gestorMapas) {
        this.gestorMapas = gestorMapas;
    }

    /**
     * Obtiene la lista de todas las entidades (personajes) en el juego.
     * @return Lista de entidades
     */
    public ArrayList<Personaje> getEntidades() {
        return entidades;
    }

    /**
     * Establece la lista de entidades del juego.
     * @param entidades Lista de entidades a establecer
     */
    public void setEntidades(ArrayList<Personaje> entidades) {
        this.entidades = entidades;
    }

    /**
     * Obtiene el personaje cuyo turno es actual.
     * @return El personaje actual o null si no hay entidades
     */
    public Personaje getPersonajeActual() {
        if (entidades.isEmpty())
            return null;
        return entidades.get(turnoIndex);
    }

    /**
     * Pasa el turno al siguiente personaje en la lista de entidades.
     */
    public void pasarTurno() {
        if (entidades.isEmpty())
            return;
        turnoIndex = (turnoIndex + 1) % entidades.size();
        notifyObservers();
    }

    /**
     * Reinicia el juego a su estado inicial.
     * Limpia los mapas, restablece al jugador y reinicia las entidades.
     */
    public void resetearJuego() {
        this.gestorMapas.clearMapas();
        this.gestorMapas = new GestorMapas();
        this.mapaActual = gestorMapas.getMapaActual();
        this.MatrizMapa = mapaActual.getMapa();
        this.entidades = new ArrayList<>();

        // Restablece al jugador a su estado inicial
        for (Heroe p : heroes) {
            if (p.getNombre().equals(jugador.getNombre())) {
                jugador = p.clone();
            }
        }

        iniciarentidades();
    }
}
