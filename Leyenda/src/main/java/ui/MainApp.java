package ui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Arquero;
import model.Defensa;
import model.Delantero;
import model.Jugador;
import model.Mediocampista;
import service.JugadorServiceImpl;
import service.ResultadoTemporada;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Interfaz JavaFX del modo carrera Leyenda. */
public class MainApp extends Application {

    private static final String CSS = "/styles/leyenda.css";
    private static final String CLUB_INICIAL = "Boca Juniors";

    private final JugadorServiceImpl jugadorService = new JugadorServiceImpl();
    private final Random random = new Random();

    private Stage stage;
    private Jugador jugadorActual;
    private String clubActual;
    private String dificultadActual = "NORMAL";
    private int temporadasPorEtapa = 2;
    private int anioActual = 2026;
    private int edadActual = 16;
    private int numeroEtapa = 1;

    private int totalPartidos;
    private int totalGoles;
    private int totalAsistencias;
    private int totalTitulos;
    private int totalBalonesOro;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("LEYENDA | Modo Carrera");
        stage.setMinWidth(1120);
        stage.setMinHeight(760);
        showHome();
        stage.show();
    }

    // ---------------------------------------------------------------------
    // INICIO Y NAVEGACIÓN
    // ---------------------------------------------------------------------

    private void showHome() {
        BorderPane shell = createShell("INICIO");
        VBox page = pageContent();
        page.getChildren().add(topBar("CENTRO DE CARRERA", "TEMPORADA 2026"));

        HBox hero = new HBox(28);
        hero.getStyleClass().add("hero-panel");
        hero.setPadding(new Insets(38));
        hero.setAlignment(Pos.CENTER_LEFT);

        VBox copy = new VBox(17);
        copy.setMaxWidth(590);
        Label eyebrow = label("EL COMIENZO DE UNA HISTORIA", "eyebrow");
        Label headline = label("NACISTE PARA\nDEJAR TU MARCA.", "hero-title");
        Label description = label(
                "Construí tu carrera desde cero. Elegí tu posición, definí tu camino y convertite en una leyenda del fútbol.",
                "hero-description");
        description.setWrapText(true);
        Button newCareer = primaryButton("CREAR NUEVA CARRERA  →");
        newCareer.setOnAction(e -> showCreatePlayer());
        Button savedPlayers = secondaryButton("MIS FUTBOLISTAS");
        savedPlayers.setOnAction(e -> showSavedPlayers());
        HBox actions = new HBox(12, newCareer, savedPlayers);
        copy.getChildren().addAll(eyebrow, headline, description, actions);
        HBox.setHgrow(copy, Priority.ALWAYS);

        StackPane visual = createHeroVisual();
        visual.setMinWidth(280);
        visual.setPrefWidth(340);
        hero.getChildren().addAll(copy, visual);

        HBox sectionHeader = new HBox();
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        sectionHeader.getChildren().addAll(
                label("TU CAMINO EMPIEZA CON TRES DECISIONES", "section-title"));

        HBox featureCards = new HBox(14,
                featureCard("01", "CREÁ TU PERFIL", "Nombre, dorsal, posición y club de debut.", "user-round-plus"),
                featureCard("02", "ELEGÍ TU RITMO", "Temporada a temporada, cada 2 o cada 4.", "calendar-days"),
                featureCard("03", "ESCRIBÍ TU HISTORIA", "Entrená, competí y acumulá estadísticas.", "trophy"));
        page.getChildren().addAll(hero, sectionHeader, featureCards);
        setPage(shell, page);
    }

    private BorderPane createShell(String active) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(createSidebar(active));
        return root;
    }

    private VBox createSidebar(String active) {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(26, 17, 22, 17));
        sidebar.setPrefWidth(224);
        sidebar.setMinWidth(224);

        HBox brand = new HBox(11);
        brand.setAlignment(Pos.CENTER_LEFT);
        StackPane mark = new StackPane();
        mark.getStyleClass().add("brand-mark");
        Label markText = label("L", "brand-mark-text");
        mark.getChildren().add(markText);
        VBox brandCopy = new VBox(1,
                label("LEYENDA", "brand-name"),
                label("PLAYER CAREER", "brand-caption"));
        brand.getChildren().addAll(mark, brandCopy);

        Label menuTitle = label("MENÚ PRINCIPAL", "sidebar-section");
        Button home = navButton("⌂", "Inicio", "INICIO".equals(active), this::showHome);
        Button create = navButton("＋", "Nueva carrera", "NUEVA".equals(active), this::showCreatePlayer);
        Button saved = navButton("▤", "Mis futbolistas", "JUGADORES".equals(active), this::showSavedPlayers);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox footer = new VBox(7);
        footer.getStyleClass().add("sidebar-footer");
        footer.getChildren().addAll(
                label("LEYENDA  /  BUILD 1.0", "sidebar-caption"),
                label("Carrera de futbolista", "sidebar-footer-title"),
                label("JavaFX  ·  MySQL", "sidebar-caption"));
        sidebar.getChildren().addAll(brand, gap(22), menuTitle, home, create, saved, spacer, footer);
        return sidebar;
    }

    private Button navButton(String icon, String text, boolean selected, Runnable action) {
        Button button = new Button(icon + "     " + text);
        button.getStyleClass().add(selected ? "nav-button-active" : "nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void setPage(BorderPane shell, VBox page) {
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        shell.setCenter(scroll);
        Scene scene = new Scene(shell, 1280, 840);
        var stylesheet = getClass().getResource(CSS);
        if (stylesheet != null) scene.getStylesheets().add(stylesheet.toExternalForm());
        stage.setScene(scene);
    }

    private VBox pageContent() {
        VBox page = new VBox(22);
        page.getStyleClass().add("page-content");
        page.setPadding(new Insets(28, 34, 36, 34));
        return page;
    }

    private HBox topBar(String title, String rightText) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(4,
                label(title, "topbar-title"),
                label("LEYENDA  /  PLAYER CAREER", "topbar-subtitle"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label tag = label(rightText, "topbar-tag");
        bar.getChildren().addAll(titles, spacer, tag);
        return bar;
    }

    private StackPane createHeroVisual() {
        StackPane art = new StackPane();
        art.getStyleClass().add("hero-art");
        Circle outer = new Circle(112);
        outer.getStyleClass().add("hero-orbit");
        Circle inner = new Circle(84);
        inner.getStyleClass().add("hero-ball");
        VBox badge = new VBox(1,
                label("OVR", "hero-art-caption"),
                label("50", "hero-art-number"),
                label("THE JOURNEY", "hero-art-caption"));
        badge.setAlignment(Pos.CENTER);
        StackPane.setAlignment(badge, Pos.CENTER);
        art.getChildren().addAll(outer, inner, badge,
                label("2026", "hero-year"));
        return art;
    }

    // ---------------------------------------------------------------------
    // CREACIÓN DE CARRERA: INCLUYE LOS TRES MODOS DE SIMULACIÓN
    // ---------------------------------------------------------------------

    private void showCreatePlayer() {
        BorderPane shell = createShell("NUEVA");
        VBox page = pageContent();
        page.getChildren().add(topBar("CREAR FUTBOLISTA", "NUEVA CARRERA"));
        page.getChildren().addAll(
                label("TU PRIMER CONTRATO", "eyebrow"),
                label("Empezá desde cero.", "page-title"),
                label("Elegí quién sos dentro de la cancha y cómo querés vivir tu carrera.", "page-subtitle"));

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre y apellido");
        TextField dorsal = new TextField("10");
        dorsal.setPromptText("1 - 99");

        ComboBox<String> posicion = new ComboBox<>();
        posicion.getItems().addAll(
                "Arquero", "Lateral Derecho", "Central Derecho", "Central Izquierdo",
                "Lateral Izquierdo", "Mediocampista", "Volante Derecho", "Volante Izquierdo",
                "Extremo Derecho", "Delantero Centro", "Extremo Izquierdo");
        posicion.setValue("Extremo Derecho");
        posicion.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> club = new ComboBox<>();
        club.getItems().addAll("Boca Juniors", "River Plate", "Racing Club", "Independiente", "San Lorenzo");
        club.setValue(CLUB_INICIAL);
        club.setMaxWidth(Double.MAX_VALUE);

        HBox row1 = new HBox(16,
                field("NOMBRE DEL JUGADOR", nombre),
                field("NÚMERO DE CAMISETA", dorsal));
        HBox row2 = new HBox(16,
                field("POSICIÓN", posicion),
                field("CLUB DE DEBUT", club));

        VBox profileCard = surfaceCard();
        profileCard.getChildren().addAll(
                sectionHeading("01", "DATOS DEL FUTBOLISTA", "Tu identidad dentro del campo"),
                row1, row2);

        ToggleGroup modeGroup = new ToggleGroup();
        ToggleButton hard = modeButton(modeGroup, "DIFÍCIL", "1 temporada por etapa", "24 etapas · máximo desafío", 1);
        ToggleButton normal = modeButton(modeGroup, "NORMAL", "2 temporadas por etapa", "12 etapas · ritmo equilibrado", 2);
        ToggleButton easy = modeButton(modeGroup, "FÁCIL", "4 temporadas por etapa", "6 etapas · progreso rápido", 4);
        normal.setSelected(true);
        HBox modeCards = new HBox(14, hard, normal, easy);

        VBox modeCard = surfaceCard();
        modeCard.getChildren().addAll(
                sectionHeading("02", "ELEGÍ EL MODO DE SIMULACIÓN", "Esta decisión determina cuántas temporadas avanzan en cada etapa."),
                modeCards,
                label("La carrera comienza a los 16 años y termina a los 40. Podés desarrollar tu OVR hasta los 29; desde los 30 comienza el declive según tu rendimiento.", "helper-text"));

        Button back = secondaryButton("VOLVER");
        back.setOnAction(e -> showHome());
        Button create = primaryButton("CREAR FUTBOLISTA Y COMENZAR  →");
        create.setOnAction(e -> {
            ToggleButton selected = (ToggleButton) modeGroup.getSelectedToggle();
            int seasons = selected == null ? 2 : (Integer) selected.getUserData();
            String mode = seasons == 1 ? "DIFÍCIL" : seasons == 2 ? "NORMAL" : "FÁCIL";
            savePlayer(nombre.getText(), dorsal.getText(), posicion.getValue(), club.getValue(), mode, seasons);
        });
        HBox actions = new HBox(12, back, create);
        actions.setAlignment(Pos.CENTER_RIGHT);

        page.getChildren().addAll(profileCard, modeCard, actions);
        setPage(shell, page);
    }

    private ToggleButton modeButton(ToggleGroup group, String title, String line1, String line2, int seasons) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setUserData(seasons);
        button.getStyleClass().add("mode-card");
        VBox content = new VBox(8,
                label(title, "mode-title"),
                label(line1, "mode-main"),
                label(line2, "mode-detail"));
        content.setAlignment(Pos.CENTER_LEFT);
        button.setGraphic(content);
        button.setText("");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(126);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private void savePlayer(String nombreTexto, String dorsalTexto, String posicion, String club,
                            String dificultad, int temporadas) {
        String nombre = nombreTexto == null ? "" : nombreTexto.trim();
        if (nombre.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Falta el nombre", "Ingresá el nombre y apellido del futbolista.");
            return;
        }
        if (posicion == null || club == null) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccioná una posición y un club.");
            return;
        }

        try {
            int numeroDorsal = Integer.parseInt(dorsalTexto.trim());
            if (numeroDorsal < 1 || numeroDorsal > 99) {
                showAlert(Alert.AlertType.WARNING, "Dorsal inválido", "El dorsal debe estar entre 1 y 99.");
                return;
            }

            Jugador nuevo = crearJugador(nombre, numeroDorsal, posicion, club);
            int id = jugadorService.crearJugador(nuevo);
            nuevo.setId(id);

            jugadorActual = nuevo;
            clubActual = club;
            dificultadActual = dificultad;
            temporadasPorEtapa = temporadas;
            anioActual = 2026;
            edadActual = 16;
            numeroEtapa = 1;
            totalPartidos = totalGoles = totalAsistencias = totalTitulos = totalBalonesOro = 0;

            showDashboard();
            showAlert(Alert.AlertType.INFORMATION, "Carrera creada", nombre + " ya forma parte de la base de datos.\nModo: " + dificultad + " · " + temporadas + " temporada(s) por etapa.");
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Dorsal inválido", "El dorsal debe ser un número entre 1 y 99.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo crear la carrera", safeMessage(ex));
        }
    }

    private Jugador crearJugador(String nombre, int dorsal, String posicion, String club) {
        Jugador jugador;
        if ("Arquero".equals(posicion)) jugador = new Arquero();
        else if (posicion.startsWith("Lateral") || posicion.startsWith("Central")) jugador = new Defensa();
        else if ("Mediocampista".equals(posicion) || posicion.startsWith("Volante")) jugador = new Mediocampista();
        else jugador = new Delantero();

        jugador.setNombre(nombre);
        jugador.setEdad(16);
        jugador.setOvr(50);
        jugador.setPrecio(500000.0);
        jugador.setResistencia(50);
        jugador.setEquipo(club);
        jugador.setDorsal(dorsal);
        jugador.setPosicion(posicion);
        jugador.setVelocidad(50);
        jugador.setRemate(50);
        jugador.setFuerza(50);
        jugador.setPase(50);
        jugador.setRegate(50);
        jugador.setCentros(50);
        jugador.setMarcaje(50);
        jugador.setDefinicion(50);
        jugador.setControl(50);
        jugador.setEntradas(50);

        if (jugador instanceof Arquero arquero) {
            arquero.setReflejos(50);
            arquero.setAtajadas(50);
            arquero.setSalida(50);
            arquero.setJuegoConPies(50);
        } else if (jugador instanceof Defensa defensa) {
            defensa.setCabezazo(50);
        } else if (jugador instanceof Mediocampista medio) {
            medio.setVision(50);
            medio.setRecuperacion(50);
        } else if (jugador instanceof Delantero delantero) {
            delantero.setCabezazo(50);
        }
        return jugador;
    }

    // ---------------------------------------------------------------------
    // PANEL DE CARRERA
    // ---------------------------------------------------------------------

    private void showDashboard() {
        if (jugadorActual == null) {
            showHome();
            return;
        }

        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("CENTRO DEL JUGADOR", "CARRERA ACTIVA"));

        HBox profile = new HBox(22);
        profile.getStyleClass().add("player-hero");
        profile.setPadding(new Insets(26));
        profile.setAlignment(Pos.CENTER_LEFT);

        StackPane shirt = new StackPane();
        shirt.getStyleClass().add("shirt-badge");
        Label dorsal = label(String.format("%02d", jugadorActual.getDorsal()), "shirt-number");
        shirt.getChildren().add(dorsal);
        shirt.setMinSize(104, 112);
        shirt.setPrefSize(104, 112);

        VBox identity = new VBox(8,
                label("PERFIL PROFESIONAL", "eyebrow"),
                label(jugadorActual.getNombre(), "player-name"),
                label(jugadorActual.getPosicion() + "  /  " + clubActual, "player-subtitle"),
                pill(dificultadActual + " · " + temporadasPorEtapa + " TEMPORADA(S) POR ETAPA"));
        HBox.setHgrow(identity, Priority.ALWAYS);

        VBox rating = new VBox(2,
                label("VALORACIÓN", "rating-caption"),
                label(String.valueOf(jugadorActual.getOvr()), "rating-number"),
                label("OVR  /  99", "rating-caption"));
        rating.setAlignment(Pos.CENTER);
        rating.getStyleClass().add("rating-panel");
        rating.setMinWidth(128);
        profile.getChildren().addAll(shirt, identity, rating);

        HBox metrics = new HBox(13,
                metricCard("EDAD", edadActual + " años", "de 16 a 40"),
                metricCard("TEMPORADA", String.valueOf(anioActual), "siguiente por jugar"),
                metricCard("ETAPA", String.valueOf(numeroEtapa), "de " + (24 / temporadasPorEtapa)),
                metricCard("MODO", dificultadActual, temporadasPorEtapa + " por avance"));

        VBox attributes = surfaceCard();
        attributes.getChildren().addAll(
                sectionHeading("RENDIMIENTO", "ATRIBUTOS DEL JUGADOR", "Valores actuales de tu futbolista"),
                attributeRow("Velocidad", jugadorActual.getVelocidad()),
                attributeRow("Remate", jugadorActual.getRemate()),
                attributeRow("Definición", jugadorActual.getDefinicion()),
                attributeRow("Pase", jugadorActual.getPase()),
                attributeRow("Regate", jugadorActual.getRegate()),
                attributeRow("Control", jugadorActual.getControl()),
                attributeRow("Fuerza", jugadorActual.getFuerza()),
                attributeRow("Marcaje", jugadorActual.getMarcaje()),
                attributeRow("Entradas", jugadorActual.getEntradas()),
                attributeRow("Resistencia", jugadorActual.getResistencia()));

        VBox actionCard = surfaceCard();
        actionCard.getChildren().addAll(
                sectionHeading("SIGUIENTE MOVIMIENTO", "PREPARÁ LA ETAPA", "Elegí cómo querés preparar a tu jugador."));
        ToggleGroup trainingGroup = new ToggleGroup();
        GridPane trainingOptions = new GridPane();
        trainingOptions.setHgap(8);
        trainingOptions.setVgap(8);
        List<String> trainingNames = new ArrayList<>(List.of("Velocidad", "Definición", "Pase y visión",
                "Regate y control", "Fuerza", "Resistencia", "Marcaje y entradas"));
        if (jugadorActual instanceof Arquero) trainingNames.add("Atajadas");
        trainingNames.add("Descanso y recuperación");
        for (int i = 0; i < trainingNames.size(); i++) {
            ToggleButton option = new ToggleButton(trainingNames.get(i));
            option.setToggleGroup(trainingGroup);
            option.setUserData(trainingNames.get(i));
            option.getStyleClass().add("training-option");
            option.setMaxWidth(Double.MAX_VALUE);
            option.setMinHeight(42);
            GridPane.setHgrow(option, Priority.ALWAYS);
            trainingOptions.add(option, i % 2, i / 2);
            if (i == 0) option.setSelected(true);
        }
        trainingOptions.setMaxWidth(Double.MAX_VALUE);
        actionCard.getChildren().addAll(
                label("ELEGÍ TU ENTRENAMIENTO", "field-label"), trainingOptions,
                label(edadActual < 30
                        ? "Cada enfoque mejora atributos diferentes y puede darte +1 OVR. Tu etapa de crecimiento termina a los 29 años."
                        : "Etapa de veterano: el OVR ya no aumenta. Desde los 30 pierde puntos por temporada según edad y rendimiento.", "helper-text"));

        Label simulationInfo = label(
                "Al avanzar se simularán " + temporadasPorEtapa + " temporada(s), salvo que llegues a los 40 años.",
                "helper-text");
        simulationInfo.setWrapText(true);
        Button simulate = primaryButton(edadActual >= 40 ? "VER RESUMEN FINAL" : "SIMULAR SIGUIENTE ETAPA  →");
        simulate.setMaxWidth(Double.MAX_VALUE);
        simulate.setOnAction(e -> {
            if (edadActual >= 40) showCareerSummary();
            else {
                ToggleButton selectedTraining = (ToggleButton) trainingGroup.getSelectedToggle();
                if (selectedTraining == null) {
                    showAlert(Alert.AlertType.WARNING, "Elegí una opción", "Seleccioná un entrenamiento o descanso antes de avanzar.");
                    return;
                }
                simulateStage((String) selectedTraining.getUserData());
            }
        });
        actionCard.getChildren().addAll(simulationInfo, simulate);

        HBox lower = new HBox(14, attributes, actionCard);
        HBox.setHgrow(attributes, Priority.ALWAYS);
        HBox.setHgrow(actionCard, Priority.ALWAYS);
        attributes.setPrefWidth(510);
        actionCard.setPrefWidth(390);

        double progress = Math.min(1.0, (edadActual - 16) / 24.0);
        VBox careerProgress = surfaceCard();
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.getStyleClass().add("career-progress");
        progressBar.setMaxWidth(Double.MAX_VALUE);
        HBox progressLabels = new HBox(
                label("PROGRESO DE CARRERA", "section-title"),
                spacer(), label(edadActual + " / 40 AÑOS", "helper-text"));
        careerProgress.getChildren().addAll(progressLabels, progressBar);

        page.getChildren().addAll(profile, metrics, lower, careerProgress);
        setPage(shell, page);
    }

    private void simulateStage(String decision) {
        if (jugadorActual == null) return;
        try {
            String trainingMessage;
            if ("Descanso y recuperación".equals(decision)) {
                jugadorActual.setResistencia(Math.min(99, jugadorActual.getResistencia() + 5));
                trainingMessage = "Descanso completo: recuperaste 5 puntos de resistencia.";
                jugadorService.actualizarJugador(jugadorActual);
            } else {
                trainingMessage = jugadorService.entrenarConEnfoque(jugadorActual, decision);
            }

            String eventMessage = resolverEventoAleatorio();
            jugadorService.actualizarJugador(jugadorActual);

            int etapaSimulada = numeroEtapa;
            int cantidad = Math.min(temporadasPorEtapa, 40 - edadActual);
            List<ResultadoTemporada> resultados = new ArrayList<>();
            List<String> declives = new ArrayList<>();
            for (int i = 0; i < cantidad; i++) {
                ResultadoTemporada resultado = jugadorService.simularTemporadaConResultado(
                        jugadorActual, clubActual, anioActual, random);
                resultados.add(resultado);
                totalPartidos += resultado.getPartidos();
                totalGoles += resultado.getGoles();
                totalAsistencias += resultado.getAsistencias();
                if (resultado.isCampeon()) totalTitulos++;
                if (resultado.isBalonOro()) totalBalonesOro++;

                // El rendimiento de ESTA temporada define cuánto se conserva el nivel al envejecer.
                declives.add(jugadorService.aplicarDeclivePorTemporada(jugadorActual, resultado));
                anioActual++;
                edadActual++;
                jugadorActual.setEdad(edadActual);
            }

            jugadorActual.setEdad(edadActual);
            jugadorActual.setEquipo(clubActual);
            jugadorService.actualizarJugador(jugadorActual);
            numeroEtapa++;
            String agingMessage = declives.stream().filter(m -> m.contains("Declive") || m.contains("sobresaliente"))
                    .reduce((a, b) -> a + " | " + b).orElse("La edad todavía no afecta tu valoración.");
            showStageResults(resultados, etapaSimulada, trainingMessage, eventMessage + " " + agingMessage);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo simular la etapa", safeMessage(ex));
        }
    }

    /** Eventos de carrera con efectos reales sobre atributos, resistencia y valoración. */
    private String resolverEventoAleatorio() {
        int evento = random.nextInt(7);
        switch (evento) {
            case 0 -> {
                jugadorActual.setVelocidad(Math.min(99, jugadorActual.getVelocidad() + 2));
                if (edadActual < 30) jugadorActual.setOvr(Math.min(99, jugadorActual.getOvr() + 1));
                return "🌟 Descubrimiento: una gran actuación llamó la atención. +2 velocidad" + (edadActual < 30 ? " y +1 OVR." : ". El OVR no aumenta después de los 29 años.");
            }
            case 1 -> {
                int perdida = random.nextInt(3) + 2;
                jugadorActual.setResistencia(Math.max(1, jugadorActual.getResistencia() - perdida));
                return "🩹 Molestia muscular: perdiste " + perdida + " puntos de resistencia. Vas a necesitar recuperación.";
            }
            case 2 -> {
                jugadorActual.setControl(Math.min(99, jugadorActual.getControl() + 2));
                jugadorActual.setPase(Math.min(99, jugadorActual.getPase() + 1));
                return "🎯 Consejo de un referente: mejoraste +2 control y +1 pase.";
            }
            case 3 -> {
                jugadorActual.setFuerza(Math.min(99, jugadorActual.getFuerza() + 2));
                return "💪 Plan físico personalizado: ganaste +2 fuerza.";
            }
            case 4 -> {
                jugadorActual.setResistencia(Math.min(99, jugadorActual.getResistencia() + 4));
                return "🔋 Semana de recuperación: recuperaste +4 resistencia.";
            }
            case 5 -> {
                if (edadActual < 30) {
                    jugadorActual.setOvr(Math.min(99, jugadorActual.getOvr() + 1));
                    return "📈 Racha de confianza: el entrenador te da más minutos. +1 OVR.";
                }
                jugadorActual.setPase(Math.min(99, jugadorActual.getPase() + 1));
                return "🧠 Experiencia: tu lectura de juego mejora (+1 pase), sin aumentar el OVR.";
            }
            default -> {
                jugadorActual.setRegate(Math.min(99, jugadorActual.getRegate() + 2));
                return "⚡ Entrenamiento extra: mejoraste +2 regate.";
            }
        }
    }

    private void showStageResults(List<ResultadoTemporada> resultados, int etapa,
                                  String trainingMessage, String eventMessage) {
        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("RESUMEN DE ETAPA", "ETAPA " + etapa));
        page.getChildren().addAll(
                label("EL CAMINO CONTINÚA", "eyebrow"),
                label("Así te fue en la cancha.", "page-title"),
                label(trainingMessage, "page-subtitle"),
                label(eventMessage, "event-banner"));

        HBox seasonCards = new HBox(14);
        for (ResultadoTemporada resultado : resultados) {
            seasonCards.getChildren().add(seasonCard(resultado));
        }

        HBox summary = new HBox(12,
                metricCard("PARTIDOS", String.valueOf(sumPartidos(resultados)), "en esta etapa"),
                metricCard("GOLES", String.valueOf(sumGoles(resultados)), "en esta etapa"),
                metricCard("ASISTENCIAS", String.valueOf(sumAsistencias(resultados)), "en esta etapa"));

        Button continueButton;
        if (edadActual >= 40) {
            continueButton = primaryButton("FINALIZAR CARRERA  →");
            continueButton.setOnAction(e -> showCareerSummary());
        } else {
            continueButton = primaryButton("VOLVER AL PANEL DE CARRERA  →");
            continueButton.setOnAction(e -> showDashboard());
        }
        Button home = secondaryButton("MENÚ PRINCIPAL");
        home.setOnAction(e -> showHome());
        HBox actions = new HBox(12, home, continueButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        page.getChildren().addAll(label("TEMPORADAS SIMULADAS", "section-title"), seasonCards,
                label("RENDIMIENTO DE LA ETAPA", "section-title"), summary, actions);
        setPage(shell, page);
    }

    private VBox seasonCard(ResultadoTemporada r) {
        VBox card = surfaceCard();
        Label year = label(String.valueOf(r.getAnio()), "season-year");
        Label club = label(r.getClub(), "player-subtitle");
        HBox miniStats = new HBox(16,
                miniStat("PJ", r.getPartidos()), miniStat("GOL", r.getGoles()), miniStat("AST", r.getAsistencias()));
        VBox awards = new VBox(5);
        if (r.isCampeon()) awards.getChildren().add(label("🏆 CAMPEÓN", "award-label"));
        if (r.isBalonOro()) awards.getChildren().add(label("★ BALÓN DE ORO", "award-label"));
        if (!r.isCampeon() && !r.isBalonOro()) awards.getChildren().add(label("TEMPORADA COMPLETADA", "helper-text"));
        card.getChildren().addAll(label("TEMPORADA", "eyebrow"), year, club, miniStats, awards);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMinWidth(190);
        return card;
    }

    private HBox miniStat(String name, int value) {
        VBox item = new VBox(3, label(name, "mini-stat-label"), label(String.valueOf(value), "mini-stat-value"));
        HBox.setHgrow(item, Priority.ALWAYS);
        return new HBox(item);
    }

    private int sumPartidos(List<ResultadoTemporada> results) { return results.stream().mapToInt(ResultadoTemporada::getPartidos).sum(); }
    private int sumGoles(List<ResultadoTemporada> results) { return results.stream().mapToInt(ResultadoTemporada::getGoles).sum(); }
    private int sumAsistencias(List<ResultadoTemporada> results) { return results.stream().mapToInt(ResultadoTemporada::getAsistencias).sum(); }

    private void showCareerSummary() {
        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("FIN DE CARRERA", "RETIRO · 40 AÑOS"));
        page.getChildren().addAll(
                label("UNA HISTORIA PARA RECORDAR", "eyebrow"),
                label("Te convertiste en una leyenda.", "page-title"),
                label(jugadorActual.getNombre() + " · " + jugadorActual.getPosicion() + " · " + clubActual, "page-subtitle"));

        HBox totals = new HBox(14,
                metricCard("PARTIDOS", String.valueOf(totalPartidos), "jugados"),
                metricCard("GOLES", String.valueOf(totalGoles), "marcados"),
                metricCard("ASISTENCIAS", String.valueOf(totalAsistencias), "generadas"),
                metricCard("TÍTULOS", String.valueOf(totalTitulos), "colectivos"),
                metricCard("BALONES DE ORO", String.valueOf(totalBalonesOro), "premios"));
        VBox finalCard = surfaceCard();
        finalCard.getChildren().addAll(
                sectionHeading("TU LEGADO", "RESUMEN DEL FUTBOLISTA", "Carrera completada"),
                detailLine("Valoración final", jugadorActual.getOvr() + " / 99"),
                detailLine("Edad de retiro", edadActual + " años"),
                detailLine("Modo de simulación", dificultadActual),
                detailLine("Temporadas por etapa", String.valueOf(temporadasPorEtapa)));

        Button players = primaryButton("VER MIS FUTBOLISTAS");
        players.setOnAction(e -> showSavedPlayers());
        Button home = secondaryButton("VOLVER AL INICIO");
        home.setOnAction(e -> showHome());
        HBox actions = new HBox(12, home, players);
        page.getChildren().addAll(totals, finalCard, actions);
        setPage(shell, page);
    }

    // ---------------------------------------------------------------------
    // JUGADORES GUARDADOS
    // ---------------------------------------------------------------------

    private void showSavedPlayers() {
        BorderPane shell = createShell("JUGADORES");
        VBox page = pageContent();
        page.getChildren().add(topBar("BASE DE DATOS", "MIS FUTBOLISTAS"));
        page.getChildren().addAll(
                label("PLANTEL PERSONAL", "eyebrow"),
                label("Tus jugadores guardados.", "page-title"),
                label("Los registros se consultan desde MySQL mediante el servicio.", "page-subtitle"));

        TableView<Jugador> table = new TableView<>();
        table.getStyleClass().add("players-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(label("Todavía no hay futbolistas guardados.", "helper-text"));

        TableColumn<Jugador, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        TableColumn<Jugador, String> nameCol = new TableColumn<>("JUGADOR");
        nameCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNombre()));
        TableColumn<Jugador, String> posCol = new TableColumn<>("POSICIÓN");
        posCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPosicion()));
        TableColumn<Jugador, String> clubCol = new TableColumn<>("CLUB");
        clubCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEquipo()));
        TableColumn<Jugador, Number> ovrCol = new TableColumn<>("OVR");
        ovrCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getOvr()));
        TableColumn<Jugador, Number> dorsalCol = new TableColumn<>("DORSAL");
        dorsalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDorsal()));
        table.getColumns().addAll(idCol, nameCol, posCol, clubCol, ovrCol, dorsalCol);
        table.setPrefHeight(440);
        loadPlayers(table);

        Button refresh = secondaryButton("ACTUALIZAR LISTA");
        refresh.setOnAction(e -> loadPlayers(table));
        Button create = primaryButton("CREAR NUEVA CARRERA  →");
        create.setOnAction(e -> showCreatePlayer());
        HBox actions = new HBox(12, refresh, create);
        page.getChildren().addAll(table, actions);
        setPage(shell, page);
    }

    private void loadPlayers(TableView<Jugador> table) {
        try {
            table.setItems(FXCollections.observableArrayList(jugadorService.listarJugadores()));
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error al consultar MySQL", safeMessage(ex));
        }
    }

    // ---------------------------------------------------------------------
    // COMPONENTES REUTILIZABLES Y ESTILOS
    // ---------------------------------------------------------------------

    private VBox field(String title, Node input) {
        Label caption = label(title, "field-label");
        input.getStyleClass().add("form-control");
        input.maxWidth(Double.MAX_VALUE);
        VBox box = new VBox(8, caption, input);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private VBox surfaceCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("surface-card");
        card.setPadding(new Insets(22));
        return card;
    }

    private VBox featureCard(String number, String title, String description, String ignoredIcon) {
        VBox card = surfaceCard();
        card.getStyleClass().add("feature-card");
        card.getChildren().addAll(
                label(number, "feature-number"),
                label(title, "feature-title"),
                label(description, "feature-description"));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox metricCard(String title, String value, String detail) {
        VBox card = new VBox(6,
                label(title, "metric-title"),
                label(value, "metric-value"),
                label(detail, "metric-detail"));
        card.getStyleClass().add("metric-card");
        card.setPadding(new Insets(17));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox sectionHeading(String number, String title, String subtitle) {
        VBox heading = new VBox(5);
        HBox line = new HBox(9, label(number, "section-number"), label(title, "section-title"));
        line.setAlignment(Pos.CENTER_LEFT);
        heading.getChildren().addAll(line, label(subtitle, "section-subtitle"));
        return heading;
    }

    private HBox attributeRow(String name, int value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label title = label(name, "attribute-name");
        Region gap = spacer();
        Label number = label(String.valueOf(value), "attribute-value");
        ProgressBar bar = new ProgressBar(Math.max(0, Math.min(1, value / 99.0)));
        bar.getStyleClass().add("attribute-progress");
        bar.setPrefWidth(170);
        row.getChildren().addAll(title, gap, bar, number);
        return row;
    }

    private HBox detailLine(String name, String value) {
        HBox row = new HBox(12, label(name, "section-subtitle"), spacer(), label(value, "attribute-value"));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label pill(String text) {
        Label label = label(text, "pill-label");
        label.getStyleClass().add("pill");
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setMinHeight(46);
        button.setPadding(new Insets(12, 20, 12, 20));
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        button.setMinHeight(46);
        button.setPadding(new Insets(12, 20, 12, 20));
        return button;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private Region gap(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        return region;
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Revisá la conexión con MySQL y los datos ingresados."
                : ex.getMessage();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "" : message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
