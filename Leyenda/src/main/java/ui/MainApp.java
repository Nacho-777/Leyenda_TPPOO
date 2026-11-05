package ui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Arquero;
import model.ClubInfo;
import model.Defensa;
import model.Delantero;
import model.Jugador;
import model.Mediocampista;
import model.OfertaTransferencia;
import model.ResultadoCompeticion;
import service.ClubesService;
import service.CompeticionesService;
import service.JugadorServiceImpl;
import service.MercadoPasesService;
import service.ResultadoTemporada;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/** Interfaz JavaFX de Leyenda: modo carrera, mercado y competiciones. */
public class MainApp extends Application {

    private static final String CSS = "/styles/leyenda.css";
    private static final int EDAD_INICIO = 16;
    private static final int EDAD_RETIRO = 40;

    private final JugadorServiceImpl jugadorService = new JugadorServiceImpl();
    private final ClubesService clubesService = new ClubesService();
    private final MercadoPasesService mercadoService = new MercadoPasesService(clubesService);
    private final CompeticionesService competicionesService = new CompeticionesService();
    private final Random random = new Random();
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

    private Stage stage;
    private Jugador jugadorActual;
    private String clubActual;
    private String dificultadActual = "NORMAL";
    private int temporadasPorEtapa = 2;
    private int anioActual = 2026;
    private int edadActual = EDAD_INICIO;
    private int numeroEtapa = 1;

    private int totalPartidos;
    private int totalGoles;
    private int totalAsistencias;
    private int totalTitulos;
    private int totalBalonesOro;
    private final List<ResultadoCompeticion> historialCompeticiones = new ArrayList<>();
    private List<OfertaTransferencia> ofertasActuales = new ArrayList<>();
    private String ultimoMovimientoMercado = "Todavía no realizaste transferencias.";

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("LEYENDA | Modo Carrera");
        stage.setMinWidth(1180);
        stage.setMinHeight(760);
        stage.setWidth(1440);
        stage.setHeight(900);
        showHome();
        stage.show();
    }

    // ---------------------------------------------------------------------
    // NAVEGACIÓN Y MENÚ PRINCIPAL
    // ---------------------------------------------------------------------

    private void showHome() {
        BorderPane shell = createShell("INICIO");
        VBox page = pageContent();
        page.getChildren().add(topBar("CENTRO DE CARRERA", "TEMPORADA " + anioActual));

        HBox hero = new HBox(28);
        hero.getStyleClass().add("hero-panel");
        hero.setPadding(new Insets(36));
        hero.setAlignment(Pos.CENTER_LEFT);

        VBox copy = new VBox(16);
        copy.setMaxWidth(650);
        copy.getChildren().addAll(
                label("MODO CARRERA · ARGENTINA + EUROPA", "eyebrow"),
                label("TU HISTORIA.\nTU CAMISETA.\nTU LEYENDA.", "hero-title"),
                label("Empezá tu carrera en Argentina o Europa, ganate un lugar, cambiá de club y competí por títulos nacionales e internacionales.", "hero-description"));
        Label modeInfo = pill(jugadorActual == null
                ? "NUEVA CARRERA DISPONIBLE"
                : "CARRERA ACTIVA · " + jugadorActual.getNombre().toUpperCase(Locale.ROOT));
        Button create = primaryButton(jugadorActual == null ? "EMPEZAR MI CARRERA  →" : "CREAR OTRA CARRERA  →");
        create.setOnAction(e -> showCreatePlayer());
        Button continueCareer = secondaryButton("CONTINUAR CARRERA");
        continueCareer.setDisable(jugadorActual == null);
        continueCareer.setOnAction(e -> showDashboard());
        copy.getChildren().addAll(modeInfo, new HBox(12, create, continueCareer));
        HBox.setHgrow(copy, Priority.ALWAYS);

        StackPane art = new StackPane();
        art.getStyleClass().add("hero-art");
        art.setMinSize(300, 290);
        art.setPrefSize(360, 290);
        Circle outer = new Circle(116);
        outer.getStyleClass().add("hero-orbit");
        Circle inner = new Circle(87);
        inner.getStyleClass().add("hero-ball");
        VBox emblem = new VBox(3,
                label("LEYENDA", "hero-art-caption"),
                label(String.valueOf(clubesService.cantidadClubes()), "hero-art-number"),
                label("CLUBES DISPONIBLES", "hero-art-caption"));
        emblem.setAlignment(Pos.CENTER);
        Label season = label("TEMPORADA 2026", "hero-year");
        art.getChildren().addAll(outer, inner, emblem, season);
        hero.getChildren().addAll(copy, art);

        HBox stats = new HBox(14,
                metricCard("CLUBES", String.valueOf(clubesService.cantidadClubes()), "Argentina + cinco ligas europeas"),
                metricCard("LIGAS", "6", "Argentina + cinco europeas"),
                metricCard("OBJETIVO", "99", "OVR máximo"),
                metricCard("CARRERA", "16–40", "años de trayectoria"));

        VBox clubSection = surfaceCard();
        clubSection.getChildren().add(sectionHeading("SCOUTING GLOBAL", "SEIS LIGAS · 126 CLUBES",
                "Elegí dónde comenzar y descubrí oportunidades en Argentina, España, Inglaterra, Italia, Francia y Portugal."));
        for (String competicion : clubesService.listarCompeticiones()) {
            List<ClubInfo> clubesLiga = clubesService.listarPorCompeticion(competicion);
            if (clubesLiga.isEmpty()) continue;
            ClubInfo referencia = clubesLiga.get(0);
            Label leagueTitle = label(referencia.getPais().toUpperCase(Locale.ROOT) + "  ·  " + competicion.toUpperCase(Locale.ROOT)
                    + "  ·  " + clubesLiga.size() + " CLUBES", "field-label");
            FlowPane clubGrid = new FlowPane(10, 10);
            clubGrid.setPrefWrapLength(870);
            for (ClubInfo club : clubesLiga) clubGrid.getChildren().add(clubMiniCard(club));
            clubSection.getChildren().addAll(leagueTitle, clubGrid);
        }

        HBox shortcuts = new HBox(14,
                shortcutCard("MERCADO DE PASES", "Recibí ofertas y cambiá de club.", "ABRIR MERCADO", this::showTransfers),
                shortcutCard("COMPETICIONES", "Copas nacionales y torneos continentales según tu país.", "VER COMPETICIONES", this::showCompetitions),
                shortcutCard("MIS FUTBOLISTAS", "Consultá los jugadores guardados en MySQL.", "VER PLANTEL", this::showSavedPlayers));
        page.getChildren().addAll(hero, stats, clubSection, shortcuts);
        setPage(shell, page);
    }

    private BorderPane createShell(String active) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(createSidebar(active));
        return root;
    }

    private VBox createSidebar(String active) {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setPrefWidth(232);
        sidebar.setMinWidth(232);

        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        StackPane mark = new StackPane();
        mark.getStyleClass().add("brand-mark");
        mark.getChildren().add(label("L", "brand-mark-text"));
        VBox brandCopy = new VBox(1,
                label("LEYENDA", "brand-name"),
                label("PLAYER CAREER MODE", "brand-caption"));
        brand.getChildren().addAll(mark, brandCopy);

        Label menuTitle = label("TU CARRERA", "sidebar-section");
        Button home = navButton("⌂", "Inicio", "INICIO".equals(active), this::showHome);
        Button create = navButton("＋", "Nueva carrera", "NUEVA".equals(active), this::showCreatePlayer);
        Button career = navButton("▣", "Mi futbolista", "CARRERA".equals(active), this::showDashboard);
        Button market = navButton("⇄", "Mercado de pases", "MERCADO".equals(active), this::showTransfers);
        Button cups = navButton("♜", "Competiciones", "COMPETICIONES".equals(active), this::showCompetitions);
        Button saved = navButton("▤", "Mis futbolistas", "JUGADORES".equals(active), this::showSavedPlayers);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox status = new VBox(6);
        status.getStyleClass().add("sidebar-footer");
        status.getChildren().addAll(
                label("TEMPORADA " + anioActual, "sidebar-caption"),
                label(jugadorActual == null ? "Sin carrera activa" : clubActual, "sidebar-footer-title"),
                label(jugadorActual == null ? "Creá tu primer jugador" : jugadorActual.getNombre(), "sidebar-caption"));
        sidebar.getChildren().addAll(brand, gap(22), menuTitle, home, create, career, market, cups, saved, spacer, status);
        return sidebar;
    }

    private Button navButton(String icon, String text, boolean selected, Runnable action) {
        Button button = new Button(icon + "     " + text);
        button.getStyleClass().add(selected ? "nav-button-active" : "nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(e -> {
            if ((text.equals("Mi futbolista") || text.equals("Mercado de pases") || text.equals("Competiciones"))
                    && jugadorActual == null) {
                showAlert(Alert.AlertType.INFORMATION, "Todavía no hay carrera", "Primero creá un futbolista para acceder a esta sección.");
                showCreatePlayer();
                return;
            }
            action.run();
        });
        return button;
    }

    private void setPage(BorderPane shell, VBox page) {
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        shell.setCenter(scroll);
        Scene scene = new Scene(shell, 1400, 900);
        var stylesheet = getClass().getResource(CSS);
        if (stylesheet != null) scene.getStylesheets().add(stylesheet.toExternalForm());
        stage.setScene(scene);
    }

    private VBox pageContent() {
        VBox page = new VBox(21);
        page.getStyleClass().add("page-content");
        page.setPadding(new Insets(27, 32, 34, 32));
        return page;
    }

    private HBox topBar(String title, String rightText) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(4,
                label(title, "topbar-title"),
                label("LEYENDA  /  PLAYER CAREER", "topbar-subtitle"));
        HBox.setHgrow(titles, Priority.ALWAYS);
        Label tag = label(rightText, "topbar-tag");
        bar.getChildren().addAll(titles, tag);
        return bar;
    }

    // ---------------------------------------------------------------------
    // CREACIÓN DE JUGADOR Y MODO DE SIMULACIÓN
    // ---------------------------------------------------------------------

    private void showCreatePlayer() {
        BorderPane shell = createShell("NUEVA");
        VBox page = pageContent();
        page.getChildren().add(topBar("CREACIÓN DE FUTBOLISTA", "NUEVO CONTRATO"));
        page.getChildren().addAll(
                label("EL PRIMER PASO", "eyebrow"),
                label("Construí tu propio camino.", "page-title"),
                label("Elegí tu identidad, tu club de debut y el ritmo de tu carrera profesional.", "page-subtitle"));

        TextField nombre = new TextField();
        nombre.setPromptText("Ej. Ignacio Gutiérrez");
        TextField dorsal = new TextField("10");
        dorsal.setPromptText("1 a 99 · se puede repetir");

        ComboBox<String> posicion = new ComboBox<>();
        posicion.getItems().addAll("Arquero", "Lateral Derecho", "Central Derecho", "Central Izquierdo",
                "Lateral Izquierdo", "Mediocampista", "Volante Derecho", "Volante Izquierdo",
                "Extremo Derecho", "Delantero Centro", "Extremo Izquierdo");
        posicion.setValue("Extremo Derecho");
        posicion.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> liga = new ComboBox<>();
        liga.getItems().add("Todas las ligas");
        liga.getItems().addAll(clubesService.listarCompeticiones());
        liga.setValue("Liga Profesional Argentina");
        liga.setMaxWidth(Double.MAX_VALUE);

        ComboBox<ClubInfo> club = new ComboBox<>();
        club.setMaxWidth(Double.MAX_VALUE);
        club.setCellFactory(list -> clubCell());
        club.setButtonCell(clubCell());
        club.setItems(FXCollections.observableArrayList(clubesService.listarPorCompeticion(liga.getValue())));
        club.setValue(clubesService.buscarPorNombre("Boca Juniors").orElse(clubesService.listarTodos().get(0)));
        HBox clubPreview = new HBox(14);
        clubPreview.getStyleClass().add("club-preview");
        clubPreview.setAlignment(Pos.CENTER_LEFT);
        updateClubPreview(clubPreview, club.getValue());
        club.valueProperty().addListener((obs, oldValue, newValue) -> updateClubPreview(clubPreview, newValue));
        liga.valueProperty().addListener((obs, oldValue, newValue) -> {
            List<ClubInfo> filtrados = clubesService.listarPorCompeticion(newValue);
            club.setItems(FXCollections.observableArrayList(filtrados));
            if (!filtrados.isEmpty()) {
                ClubInfo preferido = filtrados.stream().filter(c -> c.getNombre().equalsIgnoreCase("Boca Juniors")).findFirst()
                        .orElse(filtrados.get(0));
                club.setValue(preferido);
            } else {
                club.setValue(null);
            }
        });

        VBox dataCard = surfaceCard();
        dataCard.getChildren().addAll(
                sectionHeading("01", "PERFIL DEL FUTBOLISTA", "Nombre, camiseta y puesto dentro del campo."),
                new HBox(14, field("NOMBRE Y APELLIDO", nombre), field("DORSAL (SE PUEDE REPETIR)", dorsal)),
                new HBox(14, field("POSICIÓN", posicion), field("LIGA", liga)),
                field("CLUB DE DEBUT", club),
                clubPreview);

        ToggleGroup modeGroup = new ToggleGroup();
        ToggleButton hard = modeButton(modeGroup, "DIFÍCIL", "1 temporada por etapa", "24 etapas · cada año cuenta", 1);
        ToggleButton normal = modeButton(modeGroup, "NORMAL", "2 temporadas por etapa", "12 etapas · ritmo equilibrado", 2);
        ToggleButton easy = modeButton(modeGroup, "FÁCIL", "4 temporadas por etapa", "6 etapas · progreso rápido", 4);
        normal.setSelected(true);
        VBox modeCard = surfaceCard();
        modeCard.getChildren().addAll(
                sectionHeading("02", "ELEGÍ EL MODO DE SIMULACIÓN", "La dificultad define cuántas temporadas avanzan cada vez que continuás."),
                new HBox(13, hard, normal, easy),
                label("La carrera comienza a los 16 años y termina a los 40. El crecimiento de OVR se concentra en la etapa joven; el rendimiento y el entrenamiento ayudan a sostener el nivel.", "helper-text"));

        Button back = secondaryButton("VOLVER AL INICIO");
        back.setOnAction(e -> showHome());
        Button create = primaryButton("FIRMAR PRIMER CONTRATO  →");
        create.setOnAction(e -> {
            ToggleButton selected = (ToggleButton) modeGroup.getSelectedToggle();
            int seasons = selected == null ? 2 : (Integer) selected.getUserData();
            String mode = seasons == 1 ? "DIFÍCIL" : seasons == 2 ? "NORMAL" : "FÁCIL";
            savePlayer(nombre.getText(), dorsal.getText(), posicion.getValue(), club.getValue(), mode, seasons);
        });
        HBox actions = new HBox(12, back, create);
        actions.setAlignment(Pos.CENTER_RIGHT);
        page.getChildren().addAll(dataCard, modeCard, actions);
        setPage(shell, page);
    }

    private ListCell<ClubInfo> clubCell() {
        return new ListCell<>() {
            @Override protected void updateItem(ClubInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getNombre());
                    setGraphic(clubLogo(item, 32));
                    setContentDisplay(ContentDisplay.LEFT);
                }
            }
        };
    }

    private void updateClubPreview(HBox preview, ClubInfo club) {
        preview.getChildren().clear();
        if (club == null) return;
        VBox names = new VBox(3,
                label(club.getNombre(), "club-preview-name"),
                label(club.getProvincia() + "  ·  " + club.getPais() + "  ·  " + club.getCompeticion()
                        + "  ·  Reputación " + club.getReputacion() + "/5", "club-preview-subtitle"));
        preview.getChildren().addAll(clubLogo(club, 58), names);
    }

    private ToggleButton modeButton(ToggleGroup group, String title, String line1, String line2, int seasons) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setUserData(seasons);
        button.getStyleClass().add("mode-card");
        VBox content = new VBox(8,
                label(title, "mode-title"), label(line1, "mode-main"), label(line2, "mode-detail"));
        content.setAlignment(Pos.CENTER_LEFT);
        button.setGraphic(content);
        button.setText("");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(128);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private void savePlayer(String nombreTexto, String dorsalTexto, String posicion, ClubInfo club,
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
            Jugador nuevo = crearJugador(nombre, numeroDorsal, posicion, club.getNombre());
            int id = jugadorService.crearJugador(nuevo);
            nuevo.setId(id);

            jugadorActual = nuevo;
            clubActual = club.getNombre();
            dificultadActual = dificultad;
            temporadasPorEtapa = temporadas;
            anioActual = 2026;
            edadActual = EDAD_INICIO;
            numeroEtapa = 1;
            totalPartidos = totalGoles = totalAsistencias = totalTitulos = totalBalonesOro = 0;
            historialCompeticiones.clear();
            competicionesService.reiniciarCarrera();
            ultimoMovimientoMercado = "Todavía no realizaste transferencias.";
            showDashboard();
            showAlert(Alert.AlertType.INFORMATION, "Contrato firmado",
                    nombre + " se incorporó a " + clubActual + ".\nModo " + dificultad + " · " + temporadas + " temporada(s) por etapa.");
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
        jugador.setOvr(52);
        jugador.setPrecio(650000.0);
        jugador.setResistencia(55);
        jugador.setEquipo(club);
        jugador.setDorsal(dorsal);
        jugador.setPosicion(posicion);
        jugador.setVelocidad(52);
        jugador.setRemate(51);
        jugador.setFuerza(51);
        jugador.setPase(52);
        jugador.setRegate(52);
        jugador.setCentros(50);
        jugador.setMarcaje(48);
        jugador.setDefinicion(51);
        jugador.setControl(52);
        jugador.setEntradas(48);
        if (jugador instanceof Arquero arquero) {
            arquero.setReflejos(54); arquero.setAtajadas(54); arquero.setSalida(51); arquero.setJuegoConPies(52);
        } else if (jugador instanceof Defensa defensa) {
            defensa.setCabezazo(53); jugador.setMarcaje(54); jugador.setEntradas(54);
        } else if (jugador instanceof Mediocampista medio) {
            medio.setVision(54); medio.setRecuperacion(52); jugador.setPase(55); jugador.setControl(54);
        } else if (jugador instanceof Delantero delantero) {
            delantero.setCabezazo(51); jugador.setRemate(55); jugador.setDefinicion(55); jugador.setRegate(54);
        }
        return jugador;
    }

    // ---------------------------------------------------------------------
    // PANEL PRINCIPAL DE CARRERA
    // ---------------------------------------------------------------------

    private void showDashboard() {
        if (jugadorActual == null) { showCreatePlayer(); return; }
        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("CENTRO DEL FUTBOLISTA", "TEMPORADA " + anioActual));

        HBox hero = new HBox(20);
        hero.getStyleClass().add("player-hero");
        hero.setPadding(new Insets(24));
        hero.setAlignment(Pos.CENTER_LEFT);
        ClubInfo clubInfo = clubActualInfo();
        StackPane kit = new StackPane();
        kit.getStyleClass().add("shirt-badge");
        kit.getChildren().add(label(String.format("%02d", jugadorActual.getDorsal()), "shirt-number"));
        kit.setMinSize(100, 112);
        kit.setPrefSize(100, 112);

        VBox identity = new VBox(8,
                label("PERFIL PROFESIONAL", "eyebrow"),
                label(jugadorActual.getNombre(), "player-name"),
                label(jugadorActual.getPosicion() + "  ·  " + clubActual, "player-subtitle"),
                pill(dificultadActual + " · " + temporadasPorEtapa + " TEMPORADA(S) POR ETAPA"));
        HBox.setHgrow(identity, Priority.ALWAYS);

        VBox rating = new VBox(3, label("VALORACIÓN", "rating-caption"),
                label(String.valueOf(jugadorActual.getOvr()), "rating-number"), label("OVR / 99", "rating-caption"));
        rating.setAlignment(Pos.CENTER);
        rating.getStyleClass().add("rating-panel");
        rating.setMinWidth(126);
        hero.getChildren().addAll(clubLogo(clubInfo, 76), kit, identity, rating);

        HBox metrics = new HBox(12,
                metricCard("EDAD", edadActual + " años", "objetivo: 40"),
                metricCard("TEMPORADA", String.valueOf(anioActual), "año en curso"),
                metricCard("ETAPA", numeroEtapa + " / " + (24 / temporadasPorEtapa), temporadasPorEtapa + " temporada(s)"),
                metricCard("VALOR DE MERCADO", moneda.format(jugadorActual.getPrecio()), "estimación del juego"));

        VBox attributes = surfaceCard();
        attributes.getChildren().add(sectionHeading("SCOUTING", "ATRIBUTOS DEL JUGADOR", "Tu rendimiento actual · máximo 99"));
        attributes.getChildren().addAll(
                attributeRow("Velocidad", jugadorActual.getVelocidad()),
                attributeRow("Remate", jugadorActual.getRemate()),
                attributeRow("Definición", jugadorActual.getDefinicion()),
                attributeRow("Pase", jugadorActual.getPase()),
                attributeRow("Regate", jugadorActual.getRegate()),
                attributeRow("Control", jugadorActual.getControl()),
                attributeRow("Fuerza", jugadorActual.getFuerza()),
                attributeRow("Marcaje", jugadorActual.getMarcaje()),
                attributeRow("Entradas", jugadorActual.getEntradas()),
                attributeRow("Centros", jugadorActual.getCentros()),
                attributeRow("Resistencia", jugadorActual.getResistencia()));
        addSpecificAttributes(attributes);

        VBox progression = surfaceCard();
        progression.getChildren().addAll(
                sectionHeading("DESARROLLO", "CENTRO DE ENTRENAMIENTO", "Cinco especialidades principales según tu posición."),
                label(edadActual < 30 ? "ETAPA DE CRECIMIENTO · Cada entrenamiento puede sumar +1 OVR." : "ETAPA VETERANA · Los atributos mejoran, pero el OVR ya no sube.", "growth-status"),
                label("Elegí una de las cinco especialidades de tu puesto. El descanso es una acción adicional.", "helper-text"));
        ToggleGroup trainingGroup = new ToggleGroup();
        GridPane trainingGrid = new GridPane();
        trainingGrid.setHgap(8); trainingGrid.setVgap(8);
        List<String> trainingNames = trainingOptionsForPlayer();
        for (int i = 0; i < trainingNames.size(); i++) {
            ToggleButton option = new ToggleButton(trainingNames.get(i));
            option.setToggleGroup(trainingGroup);
            option.setUserData(trainingNames.get(i));
            option.getStyleClass().add("training-option");
            option.setMaxWidth(Double.MAX_VALUE);
            option.setMinHeight(42);
            GridPane.setHgrow(option, Priority.ALWAYS);
            trainingGrid.add(option, i % 2, i / 2);
            if (i == 0) option.setSelected(true);
        }
        progression.getChildren().addAll(label("PLAN INDIVIDUAL", "field-label"), trainingGrid,
                label("También podés ganar valoración con temporadas sobresalientes, títulos, premios y eventos aleatorios.", "helper-text"));
        Button simulate = primaryButton(edadActual >= EDAD_RETIRO ? "FINALIZAR CARRERA" : "ENTRENAR Y AVANZAR ETAPA  →");
        simulate.setMaxWidth(Double.MAX_VALUE);
        simulate.setOnAction(e -> {
            if (edadActual >= EDAD_RETIRO) { showCareerSummary(); return; }
            ToggleButton selected = (ToggleButton) trainingGroup.getSelectedToggle();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Elegí un plan", "Seleccioná un entrenamiento antes de avanzar."); return; }
            simulateStage((String) selected.getUserData());
        });
        progression.getChildren().addAll(
                label("Se simularán " + Math.min(temporadasPorEtapa, EDAD_RETIRO - edadActual) + " temporada(s) en el próximo avance.", "helper-text"),
                simulate);

        VBox quick = surfaceCard();
        quick.getChildren().add(sectionHeading("GESTIÓN", "EL PRÓXIMO PASO", "Tu carrera no se juega solamente dentro de la cancha."));
        HBox quickButtons = new HBox(10,
                primaryButton("MERCADO DE PASES  ⇄"), secondaryButton("COMPETICIONES  ♜"), secondaryButton("MIS ESTADÍSTICAS  ▤"));
        ((Button) quickButtons.getChildren().get(0)).setOnAction(e -> showTransfers());
        ((Button) quickButtons.getChildren().get(1)).setOnAction(e -> showCompetitions());
        ((Button) quickButtons.getChildren().get(2)).setOnAction(e -> showCareerSummary());
        quick.getChildren().addAll(quickButtons, label(ultimoMovimientoMercado, "helper-text"));

        HBox mainColumns = new HBox(14, attributes, progression);
        HBox.setHgrow(attributes, Priority.ALWAYS); HBox.setHgrow(progression, Priority.ALWAYS);
        attributes.setPrefWidth(520); progression.setPrefWidth(520);

        double progress = Math.min(1.0, (edadActual - EDAD_INICIO) / (double) (EDAD_RETIRO - EDAD_INICIO));
        VBox progressCard = surfaceCard();
        ProgressBar bar = new ProgressBar(progress);
        bar.getStyleClass().add("career-progress"); bar.setMaxWidth(Double.MAX_VALUE);
        HBox progressHeading = new HBox(label("PROGRESO DE CARRERA", "section-title"), spacer(),
                label(edadActual + " / " + EDAD_RETIRO + " AÑOS", "helper-text"));
        progressCard.getChildren().addAll(progressHeading, bar);

        page.getChildren().addAll(hero, metrics, mainColumns, quick, progressCard);
        setPage(shell, page);
    }

    private List<String> trainingOptionsForPlayer() {
        List<String> options = new ArrayList<>(jugadorService.obtenerEnfoquesEntrenamiento(jugadorActual));
        options.add("Descanso y recuperación");
        return options;
    }

    private void addSpecificAttributes(VBox card) {
        if (jugadorActual instanceof Arquero a) {
            card.getChildren().addAll(label("ATRIBUTOS DE ARQUERO", "attribute-section"),
                    attributeRow("Reflejos", a.getReflejos()), attributeRow("Atajadas", a.getAtajadas()),
                    attributeRow("Salidas", a.getSalida()), attributeRow("Juego con pies", a.getJuegoConPies()));
        } else if (jugadorActual instanceof Defensa d) {
            card.getChildren().addAll(label("ATRIBUTO DE DEFENSA", "attribute-section"), attributeRow("Cabezazo", d.getCabezazo()));
        } else if (jugadorActual instanceof Mediocampista m) {
            card.getChildren().addAll(label("ATRIBUTOS DE MEDIOCAMPISTA", "attribute-section"),
                    attributeRow("Visión", m.getVision()), attributeRow("Recuperación", m.getRecuperacion()));
        } else if (jugadorActual instanceof Delantero d) {
            card.getChildren().addAll(label("ATRIBUTO DE DELANTERO", "attribute-section"), attributeRow("Cabezazo", d.getCabezazo()));
        }
    }

    private void simulateStage(String decision) {
        if (jugadorActual == null) return;
        try {
            String trainingMessage;
            if ("Descanso y recuperación".equals(decision)) {
                jugadorActual.setResistencia(Math.min(99, jugadorActual.getResistencia() + 7));
                trainingMessage = "Descanso y recuperación: +7 resistencia.";
            } else {
                trainingMessage = jugadorService.entrenarConEnfoque(jugadorActual, decision);
            }
            String eventMessage = resolverEventoAleatorio();
            jugadorService.actualizarJugador(jugadorActual);

            int etapaSimulada = numeroEtapa;
            int cantidad = Math.min(temporadasPorEtapa, EDAD_RETIRO - edadActual);
            List<ResultadoTemporada> resultados = new ArrayList<>();
            List<String> aging = new ArrayList<>();
            for (int i = 0; i < cantidad; i++) {
                int temporadaFinalizada = anioActual;
                ResultadoTemporada resultado = jugadorService.simularTemporadaConResultado(
                        jugadorActual, clubActual, temporadaFinalizada, random);
                ClubInfo clubDeLaTemporada = clubActualInfo();
                List<ResultadoCompeticion> copas = competicionesService.simularTemporada(
                        jugadorActual, clubDeLaTemporada, random);
                resultado.setCompeticiones(copas);
                historialCompeticiones.addAll(copas);

                totalPartidos += resultado.getPartidos();
                totalGoles += resultado.getGoles();
                totalAsistencias += resultado.getAsistencias();
                totalBalonesOro += resultado.isBalonOro() ? 1 : 0;
                long titulos = copas.stream().filter(ResultadoCompeticion::isCampeon).count();
                totalTitulos += (int) titulos;

                String bonus = jugadorService.aplicarProgresoPorRendimiento(jugadorActual, resultado);
                String declive = jugadorService.aplicarDeclivePorTemporada(jugadorActual, resultado);
                if (!bonus.isBlank()) aging.add(bonus);
                if (declive != null && !declive.isBlank() && !declive.startsWith("Etapa de desarrollo")) aging.add(declive);

                resultados.add(resultado);
                anioActual++;
                edadActual++;
                jugadorActual.setEdad(edadActual);
                jugadorActual.setEquipo(clubActual);
                jugadorService.actualizarJugador(jugadorActual);

                // Se abre una ventana por temporada, no por etapa: en NORMAL y FÁCIL
                // el mercado aparece también entre los años simulados dentro de la misma etapa.
                if (edadActual < EDAD_RETIRO) {
                    String movimiento = mostrarMercadoFinTemporada(temporadaFinalizada);
                    if (movimiento != null && !movimiento.isBlank()) aging.add(movimiento);
                }
            }
            jugadorActual.setEdad(edadActual);
            jugadorActual.setEquipo(clubActual);
            jugadorService.actualizarJugador(jugadorActual);
            numeroEtapa++;
            String growthMessage = aging.isEmpty() ? "El progreso dependerá del próximo rendimiento." : String.join("  ·  ", aging);
            showStageResults(resultados, etapaSimulada, trainingMessage, eventMessage + " " + growthMessage);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo simular la etapa", safeMessage(ex));
        }
    }

    private String resolverEventoAleatorio() {
        int evento = random.nextInt(9);
        switch (evento) {
            case 0 -> {
                jugadorActual.setVelocidad(Math.min(99, jugadorActual.getVelocidad() + 2));
                if (edadActual < 30) jugadorActual.setOvr(Math.min(99, jugadorActual.getOvr() + 1));
                return "🌟 Oportunidad: una actuación llamó la atención del cuerpo técnico. +2 velocidad" + (edadActual < 30 ? " y +1 OVR." : "." );
            }
            case 1 -> {
                int perdida = random.nextInt(3) + 2;
                jugadorActual.setResistencia(Math.max(1, jugadorActual.getResistencia() - perdida));
                return "🩹 Molestia muscular: -" + perdida + " resistencia. Necesitás recuperar.";
            }
            case 2 -> {
                jugadorActual.setControl(Math.min(99, jugadorActual.getControl() + 2));
                jugadorActual.setPase(Math.min(99, jugadorActual.getPase() + 1));
                return "🎯 Consejo de un referente: +2 control y +1 pase.";
            }
            case 3 -> {
                jugadorActual.setFuerza(Math.min(99, jugadorActual.getFuerza() + 2));
                return "💪 Plan físico personalizado: +2 fuerza.";
            }
            case 4 -> {
                jugadorActual.setResistencia(Math.min(99, jugadorActual.getResistencia() + 4));
                return "🔋 Semana de recuperación: +4 resistencia.";
            }
            case 5 -> {
                if (edadActual < 30) jugadorActual.setOvr(Math.min(99, jugadorActual.getOvr() + 1));
                jugadorActual.setPase(Math.min(99, jugadorActual.getPase() + 1));
                return edadActual < 30 ? "📈 Racha de confianza: +1 pase y +1 OVR." : "🧠 Experiencia: +1 pase.";
            }
            case 6 -> {
                jugadorActual.setRegate(Math.min(99, jugadorActual.getRegate() + 2));
                return "⚡ Entrenamiento extra: +2 regate.";
            }
            case 7 -> {
                jugadorActual.setPrecio(jugadorActual.getPrecio() * 1.08);
                return "💹 Tu cotización subió un 8% gracias al interés de otros clubes.";
            }
            default -> {
                jugadorActual.setCentros(Math.min(99, jugadorActual.getCentros() + 2));
                return "🧤 Sesión técnica adicional: +2 centros.";
            }
        }
    }

    // ---------------------------------------------------------------------
    // MERCADO DE PASES
    // ---------------------------------------------------------------------

    private void showTransfers() {
        if (jugadorActual == null) { showAlert(Alert.AlertType.INFORMATION, "Sin carrera", "Creá un futbolista antes de entrar al mercado."); showCreatePlayer(); return; }
        ofertasActuales = mercadoService.generarOfertas(jugadorActual, clubActual, random);
        BorderPane shell = createShell("MERCADO");
        VBox page = pageContent();
        page.getChildren().add(topBar("MERCADO DE PASES", "VENTANA DE TRANSFERENCIAS"));
        page.getChildren().addAll(label("TU REPRESENTANTE", "eyebrow"), label("Hay clubes preguntando por vos.", "page-title"),
                label("Compará el proyecto deportivo, la duración del contrato y el valor de cada oferta.", "page-subtitle"));

        HBox current = new HBox(16);
        current.getStyleClass().add("market-current");
        current.setPadding(new Insets(20));
        current.setAlignment(Pos.CENTER_LEFT);
        current.getChildren().addAll(clubLogo(clubActualInfo(), 62),
                new VBox(5, label("CLUB ACTUAL", "eyebrow"), label(clubActual, "club-preview-name"),
                        label("Valor estimado: " + moneda.format(jugadorActual.getPrecio()), "club-preview-subtitle")), spacer(),
                pill("OVR " + jugadorActual.getOvr() + " · " + edadActual + " AÑOS"));

        FlowPane offersGrid = new FlowPane(14, 14);
        for (OfertaTransferencia offer : ofertasActuales) offersGrid.getChildren().add(offerCard(offer));
        VBox note = surfaceCard();
        note.getChildren().addAll(sectionHeading("NEGOCIACIÓN", "ÚLTIMO MOVIMIENTO", ultimoMovimientoMercado),
                label("Al terminar cada temporada recibís una nueva ventana de pases. Los dorsales no son exclusivos: varios futbolistas pueden usar el mismo número.", "helper-text"));
        Button refresh = secondaryButton("BUSCAR NUEVAS OFERTAS");
        refresh.setOnAction(e -> showTransfers());
        Button back = primaryButton("VOLVER A MI CARRERA");
        back.setOnAction(e -> showDashboard());
        HBox actions = new HBox(12, refresh, back);
        page.getChildren().addAll(current, label("OFERTAS RECIBIDAS", "section-title"), offersGrid, note, actions);
        setPage(shell, page);
    }

    private VBox offerCard(OfertaTransferencia offer) {
        ClubInfo club = offer.getClub();
        VBox card = surfaceCard();
        card.getStyleClass().add("offer-card");
        HBox head = new HBox(12, clubLogo(club, 58),
                new VBox(4, label(club.getNombre(), "club-card-title"),
                        label(club.getProvincia() + " · " + club.getPais() + " · " + club.getCompeticion(), "club-preview-subtitle")));
        head.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(head,
                metricLine("VALOR DEL FICHAJE", moneda.format(offer.getValorMercado())),
                metricLine("SALARIO ANUAL", moneda.format(offer.getSalarioAnual())),
                metricLine("CONTRATO", offer.getAnosContrato() + " temporadas"),
                label(offer.getMotivo(), "helper-text"));
        Button accept = primaryButton("ACEPTAR OFERTA  →");
        accept.setMaxWidth(Double.MAX_VALUE);
        accept.setOnAction(e -> aceptarOferta(offer));
        card.getChildren().add(accept);
        card.setPrefWidth(310);
        card.setMinWidth(290);
        return card;
    }

    private HBox metricLine(String key, String value) {
        HBox line = new HBox(8, label(key, "metric-title"), spacer(), label(value, "metric-value-small"));
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }

    private void aceptarOferta(OfertaTransferencia oferta) {
        try {
            String mensaje = registrarTransferencia(oferta, anioActual);
            showAlert(Alert.AlertType.INFORMATION, "¡Fichaje confirmado!", mensaje);
            showDashboard();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "No se pudo completar el fichaje", safeMessage(ex));
        }
    }

    private String registrarTransferencia(OfertaTransferencia oferta, int temporada) throws Exception {
        String anterior = clubActual;
        clubActual = oferta.getClub().getNombre();
        jugadorActual.setEquipo(clubActual);
        jugadorActual.setPrecio(oferta.getValorMercado());
        jugadorService.actualizarJugador(jugadorActual);
        ultimoMovimientoMercado = "Transferencia: " + anterior + " → " + clubActual
                + ". Contrato por " + oferta.getAnosContrato() + " temporadas.";
        return "Temporada " + temporada + ": " + jugadorActual.getNombre() + " pasa de "
                + anterior + " a " + clubActual + ".\nValor: " + moneda.format(oferta.getValorMercado())
                + " · Salario anual: " + moneda.format(oferta.getSalarioAnual()) + ".";
    }

    /** Se abre automáticamente al cerrar cada temporada (incluso dentro de una etapa de 2 o 4 años). */
    private String mostrarMercadoFinTemporada(int temporada) throws Exception {
        ofertasActuales = mercadoService.generarOfertas(jugadorActual, clubActual, random);
        if (ofertasActuales.isEmpty()) {
            ultimoMovimientoMercado = "Temporada " + temporada + ": no llegaron ofertas. Seguís en " + clubActual + ".";
            return ultimoMovimientoMercado;
        }

        Dialog<OfertaTransferencia> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("LEYENDA · MERCADO DE PASES");
        dialog.setHeaderText("Terminó la temporada " + temporada + " · ¿Cuál es tu próximo paso?");
        dialog.setResizable(true);
        dialog.getDialogPane().getStyleClass().add("season-market-dialog");

        ListView<OfertaTransferencia> lista = new ListView<>(FXCollections.observableArrayList(ofertasActuales));
        lista.setPrefSize(560, 285);
        lista.getStyleClass().add("market-offer-list");
        lista.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(OfertaTransferencia oferta, boolean empty) {
                super.updateItem(oferta, empty);
                if (empty || oferta == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ClubInfo destino = oferta.getClub();
                    setText(destino.getNombre() + "  ·  " + destino.getPais() + " / " + destino.getCompeticion()
                            + "\nReputación " + destino.getReputacion() + "/5  ·  Valor "
                            + moneda.format(oferta.getValorMercado()) + "  ·  Contrato "
                            + oferta.getAnosContrato() + " temporadas");
                    setGraphic(clubLogo(destino, 38));
                    setContentDisplay(ContentDisplay.LEFT);
                }
            }
        });
        VBox contenido = new VBox(10,
                label("TU CLUB ACTUAL: " + clubActual, "club-card-title"),
                label("Elegí una oferta o quedate. El cambio se guarda automáticamente en la base de datos.", "helper-text"),
                lista);
        contenido.setPadding(new Insets(8, 2, 4, 2));
        dialog.getDialogPane().setContent(contenido);

        ButtonType aceptar = new ButtonType("ACEPTAR FICHAJE", ButtonBar.ButtonData.OK_DONE);
        ButtonType quedarse = new ButtonType("QUEDARME EN " + clubActual.toUpperCase(Locale.ROOT), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(aceptar, quedarse);
        dialog.setResultConverter(tipo -> tipo == aceptar ? lista.getSelectionModel().getSelectedItem() : null);
        Node botonAceptar = dialog.getDialogPane().lookupButton(aceptar);
        botonAceptar.setDisable(true);
        lista.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) ->
                botonAceptar.setDisable(seleccionada == null));
        if (getClass().getResource(CSS) != null) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        }

        OfertaTransferencia seleccion = dialog.showAndWait().orElse(null);
        if (seleccion != null) {
            return registrarTransferencia(seleccion, temporada);
        }
        ultimoMovimientoMercado = "Temporada " + temporada + ": decidiste quedarte en " + clubActual + ".";
        return ultimoMovimientoMercado;
    }

    // ---------------------------------------------------------------------
    // COMPETICIONES NACIONALES E INTERNACIONALES
    // ---------------------------------------------------------------------

    private void showCompetitions() {
        if (jugadorActual == null) { showAlert(Alert.AlertType.INFORMATION, "Sin carrera", "Creá un futbolista para consultar sus competiciones."); showCreatePlayer(); return; }
        BorderPane shell = createShell("COMPETICIONES");
        VBox page = pageContent();
        page.getChildren().add(topBar("CALENDARIO DE COMPETICIONES", "TEMPORADA " + anioActual));
        page.getChildren().addAll(label("EL SUEÑO DE TODO FUTBOLISTA", "eyebrow"),
                label("Competí por todo.", "page-title"),
                label("Los torneos se resuelven al simular cada temporada. La clasificación internacional depende del nivel y la reputación del club.", "page-subtitle"));

        ClubInfo clubInfoActual = clubActualInfo();
        String paisActual = clubInfoActual.getPais();
        VBox national = surfaceCard();
        national.getChildren().add(sectionHeading(paisActual.toUpperCase(Locale.ROOT),
                "COMPETICIONES NACIONALES", "Calendario adaptado a la liga del club donde jugás actualmente."));
        List<String[]> nacionales = switch (paisActual) {
            case "España" -> List.of(
                    new String[]{"LaLiga", "Liga de primera división"},
                    new String[]{"Copa del Rey", "Copa nacional por eliminación directa"},
                    new String[]{"Supercopa de España", "Competición entre los mejores de la temporada"});
            case "Inglaterra" -> List.of(
                    new String[]{"Premier League", "Liga de primera división"},
                    new String[]{"FA Cup", "Copa nacional histórica"},
                    new String[]{"EFL Cup", "Copa de la liga inglesa"},
                    new String[]{"FA Community Shield", "Desafío entre campeones"});
            case "Italia" -> List.of(
                    new String[]{"Serie A", "Liga de primera división"},
                    new String[]{"Coppa Italia", "Copa nacional"},
                    new String[]{"Supercoppa Italiana", "Supercopa nacional"});
            case "Francia" -> List.of(
                    new String[]{"Ligue 1", "Liga de primera división"},
                    new String[]{"Coupe de France", "Copa nacional"},
                    new String[]{"Trophée des Champions", "Supercopa francesa"});
            case "Portugal" -> List.of(
                    new String[]{"Liga Portugal", "Liga de primera división"},
                    new String[]{"Taça de Portugal", "Copa nacional"},
                    new String[]{"Taça da Liga", "Copa de la liga"},
                    new String[]{"Supertaça Cândido de Oliveira", "Supercopa portuguesa"});
            default -> List.of(
                    new String[]{"Torneo Apertura", "Liga · Torneo de primera mitad de año"},
                    new String[]{"Torneo Clausura", "Liga · Torneo de segunda mitad de año"},
                    new String[]{"Copa Argentina", "Copa nacional · eliminación directa"},
                    new String[]{"Campeón de Liga", "Tabla general anual"},
                    new String[]{"Trofeo de Campeones", "Enfrenta a campeones del Apertura y Clausura"},
                    new String[]{"Supercopa Argentina", "Campeones nacionales"},
                    new String[]{"Supercopa Internacional", "Título entre campeones de la temporada"},
                    new String[]{"Recopa de Campeones", "Reúne a ganadores de copas nacionales e internacionales"});
        };
        for (String[] torneo : nacionales) national.getChildren().add(competitionTile(torneo[0], torneo[1], "NACIONAL"));

        VBox international = surfaceCard();
        boolean argentina = "Argentina".equals(paisActual);
        international.getChildren().add(sectionHeading(argentina ? "CONMEBOL" : "UEFA",
                "COMPETICIONES INTERNACIONALES", "La clasificación depende del OVR del futbolista y la reputación del club."));
        if (argentina) {
            international.getChildren().addAll(
                    competitionTile("Copa Libertadores", "El máximo objetivo continental de Sudamérica", "INTERNACIONAL"),
                    competitionTile("Copa Sudamericana", "La otra gran copa de clubes de América", "INTERNACIONAL"),
                    competitionTile("Recopa Sudamericana", "Entre los campeones de Libertadores y Sudamericana", "INTERNACIONAL"));
        } else {
            international.getChildren().addAll(
                    competitionTile("UEFA Champions League", "La máxima competición europea de clubes", "INTERNACIONAL"),
                    competitionTile("UEFA Europa League", "El segundo torneo continental", "INTERNACIONAL"),
                    competitionTile("UEFA Conference League", "Una oportunidad internacional para más clubes", "INTERNACIONAL"),
                    competitionTile("UEFA Super Cup", "Entre campeones de Champions y Europa League", "INTERNACIONAL"));
        }

        VBox history = surfaceCard();
        history.getChildren().add(sectionHeading("HISTORIAL", "TUS RESULTADOS", "Se muestran las competiciones simuladas durante esta carrera."));
        if (historialCompeticiones.isEmpty()) {
            history.getChildren().add(label("Todavía no jugaste una temporada. Avanzá desde el panel de carrera para generar resultados.", "helper-text"));
        } else {
            for (int i = historialCompeticiones.size() - 1; i >= 0; i--) {
                ResultadoCompeticion result = historialCompeticiones.get(i);
                history.getChildren().add(resultLine(result));
            }
        }
        Button back = primaryButton("VOLVER A MI CARRERA");
        back.setOnAction(e -> showDashboard());
        page.getChildren().addAll(new HBox(14, national, international), history, back);
        setPage(shell, page);
    }

    private VBox competitionTile(String name, String description, String type) {
        HBox row = new HBox(12, pill(type), new VBox(3, label(name, "club-card-title"), label(description, "helper-text")));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("competition-tile");
        VBox wrapper = new VBox(row);
        return wrapper;
    }

    private HBox resultLine(ResultadoCompeticion result) {
        Label badge = pill(result.isCampeon() ? "CAMPEÓN" : result.getCategoria());
        VBox copy = new VBox(3, label(result.getNombre(), "club-card-title"), label(result.getFase() + " · " + result.getDetalle(), "helper-text"));
        HBox row = new HBox(12, badge, copy, spacer());
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("result-line");
        return row;
    }

    // ---------------------------------------------------------------------
    // RESULTADOS DE ETAPA Y RESUMEN DE CARRERA
    // ---------------------------------------------------------------------

    private void showStageResults(List<ResultadoTemporada> resultados, int etapa,
                                  String trainingMessage, String eventMessage) {
        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("INFORME DE RENDIMIENTO", "ETAPA " + etapa));
        page.getChildren().addAll(label("EL CAMINO CONTINÚA", "eyebrow"),
                label("Así se escribió esta etapa.", "page-title"),
                label(trainingMessage, "page-subtitle"), label(eventMessage, "event-banner"));

        FlowPane seasonCards = new FlowPane(14, 14);
        for (ResultadoTemporada result : resultados) seasonCards.getChildren().add(seasonCard(result));
        HBox summary = new HBox(12,
                metricCard("PARTIDOS", String.valueOf(sumPartidos(resultados)), "en esta etapa"),
                metricCard("GOLES", String.valueOf(sumGoles(resultados)), "en esta etapa"),
                metricCard("ASISTENCIAS", String.valueOf(sumAsistencias(resultados)), "en esta etapa"),
                metricCard("OVR ACTUAL", jugadorActual.getOvr() + " / 99", "valoración"));

        Button continueButton;
        if (edadActual >= EDAD_RETIRO) {
            continueButton = primaryButton("CERRAR CARRERA  →");
            continueButton.setOnAction(e -> showCareerSummary());
        } else {
            continueButton = primaryButton("VOLVER AL CENTRO DE CARRERA  →");
            continueButton.setOnAction(e -> showDashboard());
        }
        Button market = secondaryButton("REVISAR OFERTAS"); market.setOnAction(e -> showTransfers());
        Button home = secondaryButton("MENÚ PRINCIPAL"); home.setOnAction(e -> showHome());
        page.getChildren().addAll(label("TEMPORADAS Y COMPETICIONES", "section-title"), seasonCards,
                label("RENDIMIENTO DE LA ETAPA", "section-title"), summary, new HBox(10, home, market, continueButton));
        setPage(shell, page);
    }

    private VBox seasonCard(ResultadoTemporada result) {
        VBox card = surfaceCard();
        card.getStyleClass().add("season-card");
        HBox header = new HBox(10, label(String.valueOf(result.getAnio()), "season-year"), spacer(), pill(result.getClub()));
        HBox miniStats = new HBox(15, miniStat("PJ", result.getPartidos()), miniStat("GOL", result.getGoles()), miniStat("AST", result.getAsistencias()));
        VBox cups = new VBox(6);
        for (ResultadoCompeticion comp : result.getCompeticiones()) {
            Label line = label((comp.isCampeon() ? "🏆 " : "• ") + comp.getNombre() + " · " + comp.getFase(), comp.isCampeon() ? "award-label" : "competition-detail");
            line.setWrapText(true);
            cups.getChildren().add(line);
        }
        if (cups.getChildren().isEmpty()) cups.getChildren().add(label("Temporada completada", "helper-text"));
        card.getChildren().addAll(header, miniStats, label("COMPETICIONES", "field-label"), cups);
        card.setPrefWidth(390); card.setMinWidth(340);
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
        if (jugadorActual == null) { showHome(); return; }
        BorderPane shell = createShell("CARRERA");
        VBox page = pageContent();
        page.getChildren().add(topBar("CIERRE DE CARRERA", "RETIRO · " + edadActual + " AÑOS"));
        page.getChildren().addAll(label("UNA HISTORIA PARA RECORDAR", "eyebrow"),
                label("Tu nombre queda en la historia.", "page-title"),
                label(jugadorActual.getNombre() + " · " + jugadorActual.getPosicion() + " · " + clubActual, "page-subtitle"));
        HBox totals = new HBox(12,
                metricCard("PARTIDOS", String.valueOf(totalPartidos), "jugados"),
                metricCard("GOLES", String.valueOf(totalGoles), "marcados"),
                metricCard("ASISTENCIAS", String.valueOf(totalAsistencias), "generadas"),
                metricCard("TÍTULOS", String.valueOf(totalTitulos), "competiciones"),
                metricCard("BALONES DE ORO", String.valueOf(totalBalonesOro), "premios individuales"));
        VBox finalCard = surfaceCard();
        finalCard.getChildren().addAll(sectionHeading("TU LEGADO", "RESUMEN DEL FUTBOLISTA", "Carrera completada"),
                detailLine("Valoración final", jugadorActual.getOvr() + " / 99"),
                detailLine("Club final", clubActual),
                detailLine("Valor de mercado", moneda.format(jugadorActual.getPrecio())),
                detailLine("Modo de simulación", dificultadActual),
                detailLine("Temporadas por etapa", String.valueOf(temporadasPorEtapa)));
        Button cups = secondaryButton("VER HISTORIAL DE COMPETICIONES"); cups.setOnAction(e -> showCompetitions());
        Button players = primaryButton("VER MIS FUTBOLISTAS"); players.setOnAction(e -> showSavedPlayers());
        Button home = secondaryButton("VOLVER AL INICIO"); home.setOnAction(e -> showHome());
        Button challenge = primaryButton("JUGAR DESAFÍO FINAL 1 VS 1  ⚽");
        challenge.setOnAction(e -> OneVsOneGame.start(stage, jugadorActual, clubActual, this::showCareerSummary));
        challenge.setMaxWidth(Double.MAX_VALUE);
        VBox challengeCard = surfaceCard();
        challengeCard.getChildren().addAll(
                label("EL ÚLTIMO CAPÍTULO", "eyebrow"),
                label("Demostrá que sos una Leyenda. Enfrentá a los cinco grandes en un desafío 1 vs 1.", "page-subtitle"),
                challenge);
        page.getChildren().addAll(totals, finalCard, challengeCard, new HBox(10, home, cups, players));
        setPage(shell, page);
    }

    // ---------------------------------------------------------------------
    // JUGADORES GUARDADOS EN MYSQL
    // ---------------------------------------------------------------------

    private void showSavedPlayers() {
        BorderPane shell = createShell("JUGADORES");
        VBox page = pageContent();
        page.getChildren().add(topBar("BASE DE DATOS", "MIS FUTBOLISTAS"));
        page.getChildren().addAll(label("PLANTEL PERSONAL", "eyebrow"), label("Tus futbolistas guardados.", "page-title"),
                label("Los registros se consultan desde MySQL mediante JugadorService.", "page-subtitle"));
        TableView<Jugador> table = new TableView<>();
        table.getStyleClass().add("players-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(label("Todavía no hay futbolistas guardados.", "helper-text"));
        TableColumn<Jugador, Number> id = new TableColumn<>("ID"); id.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getId()));
        TableColumn<Jugador, String> name = new TableColumn<>("FUTBOLISTA"); name.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getNombre()));
        TableColumn<Jugador, String> position = new TableColumn<>("POSICIÓN"); position.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPosicion()));
        TableColumn<Jugador, String> team = new TableColumn<>("CLUB"); team.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getEquipo()));
        TableColumn<Jugador, Number> rating = new TableColumn<>("OVR"); rating.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getOvr()));
        TableColumn<Jugador, Number> number = new TableColumn<>("DORSAL"); number.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getDorsal()));
        table.getColumns().addAll(id, name, position, team, rating, number);
        table.setPrefHeight(510);
        loadPlayers(table);
        Button refresh = secondaryButton("ACTUALIZAR LISTA"); refresh.setOnAction(e -> loadPlayers(table));
        Button create = primaryButton("CREAR NUEVA CARRERA  →"); create.setOnAction(e -> showCreatePlayer());
        page.getChildren().addAll(table, new HBox(10, refresh, create));
        setPage(shell, page);
    }

    private void loadPlayers(TableView<Jugador> table) {
        try { table.setItems(FXCollections.observableArrayList(jugadorService.listarJugadores())); }
        catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, "Error al consultar MySQL", safeMessage(ex)); }
    }

    // ---------------------------------------------------------------------
    // COMPONENTES VISUALES REUTILIZABLES
    // ---------------------------------------------------------------------

    private HBox clubMiniCard(ClubInfo club) {
        Label fullName = label(club.getNombre(), "club-mini-detail");
        fullName.setWrapText(true);
        VBox names = new VBox(2, label(club.getAbreviatura(), "club-mini-name"), fullName);
        HBox card = new HBox(8, clubLogo(club, 38), names);
        card.getStyleClass().add("club-mini-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(190);
        card.setMinHeight(66);
        return card;
    }

    private Node clubLogo(ClubInfo club, double size) {
        if (club == null) return label("LPF", "brand-mark-text");
        var stream = getClass().getResourceAsStream(club.getLogoResource());
        if (stream == null) {
            StackPane fallback = new StackPane(label(club.getAbreviatura(), "club-logo-fallback"));
            fallback.setMinSize(size, size); fallback.setPrefSize(size, size);
            fallback.setStyle("-fx-background-color: " + club.getColorPrimario() + "; -fx-background-radius: 12;");
            return fallback;
        }
        ImageView image = new ImageView(new Image(stream));
        image.setFitWidth(size); image.setFitHeight(size); image.setPreserveRatio(true);
        image.getStyleClass().add("club-logo");
        return image;
    }

    private ClubInfo clubActualInfo() {
        return clubesService.buscarPorNombre(clubActual).orElse(clubesService.listarTodos().get(0));
    }

    private VBox shortcutCard(String title, String description, String actionText, Runnable action) {
        VBox card = surfaceCard();
        card.getStyleClass().add("shortcut-card");
        Label titleLabel = label(title, "shortcut-title");
        Label descriptionLabel = label(description, "feature-description");
        descriptionLabel.setWrapText(true);
        Button actionButton = secondaryButton(actionText);
        actionButton.setOnAction(e -> action.run());
        card.getChildren().addAll(titleLabel, descriptionLabel, actionButton);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPrefWidth(300);
        return card;
    }

    private VBox field(String title, Node input) {
        Label caption = label(title, "field-label");
        input.getStyleClass().add("form-control");
        input.setStyle(input.getStyle());
        VBox box = new VBox(8, caption, input);
        HBox.setHgrow(box, Priority.ALWAYS); box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private VBox surfaceCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("surface-card");
        card.setPadding(new Insets(21));
        return card;
    }

    private VBox metricCard(String title, String value, String detail) {
        VBox card = new VBox(6, label(title, "metric-title"), label(value, "metric-value"), label(detail, "metric-detail"));
        card.getStyleClass().add("metric-card"); card.setPadding(new Insets(16));
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
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label title = label(name, "attribute-name");
        Label number = label(String.valueOf(value), "attribute-value");
        ProgressBar bar = new ProgressBar(Math.max(0, Math.min(1, value / 99.0)));
        bar.getStyleClass().add("attribute-progress"); bar.setPrefWidth(150);
        row.getChildren().addAll(title, spacer(), bar, number);
        return row;
    }

    private HBox detailLine(String name, String value) {
        HBox row = new HBox(12, label(name, "section-subtitle"), spacer(), label(value, "attribute-value"));
        row.setAlignment(Pos.CENTER_LEFT); return row;
    }

    private Label pill(String text) {
        Label value = label(text, "pill-label"); value.getStyleClass().add("pill"); return value;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text); button.getStyleClass().add("primary-button");
        button.setMinHeight(44); button.setPadding(new Insets(12, 18, 12, 18)); return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text); button.getStyleClass().add("secondary-button");
        button.setMinHeight(44); button.setPadding(new Insets(12, 18, 12, 18)); return button;
    }

    private Label label(String text, String styleClass) {
        Label value = new Label(text); value.getStyleClass().add(styleClass); return value;
    }

    private Region spacer() {
        Region region = new Region(); HBox.setHgrow(region, Priority.ALWAYS); return region;
    }

    private Region gap(double height) {
        Region region = new Region(); region.setMinHeight(height); region.setPrefHeight(height); return region;
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Revisá la conexión con MySQL y los datos ingresados." : ex.getMessage();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(message == null ? "" : message); alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
