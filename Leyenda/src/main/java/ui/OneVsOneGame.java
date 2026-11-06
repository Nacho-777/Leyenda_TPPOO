package ui;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Jugador;
import model.Defensa;
import model.Delantero;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Desafío final 1 vs 1 de Leyenda. Mecánicas propias inspiradas en el género
 * Football Heads; no utiliza ni copia código interno de DVADI.
 */
public final class OneVsOneGame {
    private static final double W = 1000, H = 560, FLOOR = 480;
    private static final String[] OPPONENTS = {
            "RACING", "SAN LORENZO", "INDEPENDIENTE", "RIVER PLATE", "BOCA JUNIORS"
    };
    private static final String[] SHORT_NAMES = {"RAC", "CASLA", "CAI", "RIV", "BOC"};

    private final Stage stage;
    private final Jugador player;
    private final String club;
    private final Runnable onExit;
    private final List<String> matchLog = new ArrayList<>();
    private int wins, losses, nextOpponent;
    private int userGoals, botGoals;
    private int practiceGoals, practiceBotGoals;
    private boolean practice = true;
    private boolean finished;
    private boolean extraTime;
    private double remaining = 90;
    private double playerX = 155, playerY = FLOOR - 76, playerVY;
    private double botX = 845, botY = FLOOR - 76, botVY;
    private double ballX = W / 2, ballY = FLOOR - 30, ballVX = 0, ballVY = 0;
    private double playerCooldown, botCooldown, botJumpCooldown, playerHeaderCooldown, botHeaderCooldown, elapsed;
    private double ballStuckTimer;
    // Pegá aquí una URL directa a un PNG/JPG de la pelota Trionda 2026. También acepta -Dleyenda.ballImageUrl=URL.
    private static final String BALL_IMAGE_URL = System.getProperty("leyenda.ballImageUrl", "");
    private final Image ballImage = loadBallImage();
    private boolean left, right, jump, shoot;
    private long lastFrame;
    private AnimationTimer timer;
    private Canvas canvas;
    private Label statusLabel, scoreLabel, clockLabel;
    private int aiLevel = 1;
    private String aiDescription = "LEYENDA";
    private boolean practiceFinished;
    private boolean importantMatch;
    private String importantMatchTitle = "PARTIDO IMPORTANTE";
    private Consumer<Boolean> importantMatchResult;

    private OneVsOneGame(Stage stage, Jugador player, String club, Runnable onExit) {
        this.stage = stage;
        this.player = player;
        this.club = club == null ? player.getEquipo() : club;
        this.onExit = onExit;
    }

    public static void start(Stage stage, Jugador player, String club, Runnable onExit) {
        new OneVsOneGame(stage, player, club, onExit).showTutorial();
    }

    public static void startImportantMatch(Stage stage, Jugador player, String club,
                                           String title, Consumer<Boolean> resultHandler, Runnable returnToPage) {
        OneVsOneGame game = new OneVsOneGame(stage, player, club, returnToPage);
        game.importantMatch = true;
        game.importantMatchTitle = title == null ? "PARTIDO IMPORTANTE" : title;
        game.importantMatchResult = resultHandler;
        game.showImportantMatchIntro();
    }

    private void showImportantMatchIntro() {
        BorderPane root = shell();
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(36));
        box.getChildren().addAll(
                label("PARTIDO OFICIAL · " + java.time.LocalDate.now().getYear() + " · DIFICULTAD LEYENDA", 13, "#6CE5FF"),
                label(importantMatchTitle, 32, "#F4FBFF"),
                label(player.getNombre() + " · " + player.getOvr() + " OVR · " + club, 14, "#BBD6E8"),
                label("Un solo partido de 90 segundos. Si ganás, registrás la victoria; si perdés, queda registrada la derrota.", 14, "#BBD6E8"),
                label("← → mover   ·   ↑ saltar   ·   ESPACIO patear con el pie", 13, "#72E5FF"));
        Button play = button("JUGAR PARTIDO  →", true);
        play.setOnAction(e -> startMatch(false));
        Button cancel = button("VOLVER A COMPETICIONES", false);
        cancel.setOnAction(e -> onExit.run());
        HBox actions = new HBox(12, cancel, play);
        actions.setAlignment(Pos.CENTER);
        box.getChildren().add(actions);
        root.setCenter(box);
        setScene(root);
    }

    private void showTutorial() {
        stopTimer();
        BorderPane root = shell();
        VBox content = new VBox(18);
        content.setPadding(new Insets(34));
        content.setAlignment(Pos.CENTER);
        Label eyebrow = label("EL DESAFÍO FINAL · LEYENDA", 12, "#62DFFF");
        Label title = label("FÚTBOL HEADS 1 VS 1", 34, "#F2FAFF");
        Label subtitle = label("Tu carrera termina en la cancha. Jugá con el futbolista que creaste.", 14, "#A9C5DC");
        VBox controls = new VBox(13);
        controls.setMaxWidth(650);
        controls.setPadding(new Insets(22));
        controls.setStyle(cardStyle());
        controls.getChildren().addAll(
                controlRow("←  →", "MOVERSE", "Desplazate por la cancha para buscar la pelota."),
                controlRow("↑", "SALTAR", "Saltá para disputar la pelota en el aire."),
                controlRow("ESPACIO", "PEGARLE", "Golpeá la pelota hacia el arco rival."),
                controlRow("90 SEGUNDOS", "DURACIÓN", "Gana quien marque más goles antes de que termine el reloj."));
        Label rules = label("Primero vas a jugar una práctica NORMAL. Después, el desafío Leyenda: hasta 5 partidos y necesitás ganar 3.", 13, "#B7D5E8");
        rules.setWrapText(true);
        Label avatar = label(player.getNombre().toUpperCase(Locale.ROOT) + "  ·  " + player.getOvr() + " OVR  ·  " + club, 13, "#79E8FF");
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        Button back = button("VOLVER A LA CARRERA", false);
        back.setOnAction(e -> onExit.run());
        Button begin = button("EMPEZAR PRÁCTICA  →", true);
        begin.setOnAction(e -> startMatch(true));
        actions.getChildren().addAll(back, begin);
        content.getChildren().addAll(eyebrow, title, subtitle, avatar, controls, rules, actions);
        root.setCenter(content);
        Scene scene = new Scene(root, Math.max(720, stage.getWidth()), Math.max(520, stage.getHeight()));
        scene.getStylesheets().add(css());
        scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.F11) stage.setFullScreen(!stage.isFullScreen()); });
        stage.setScene(scene);
        stage.setTitle("LEYENDA | Desafío final 1 vs 1");
        stage.show();

        // Animación de los controles para que se entiendan antes del primer partido.
        controls.getChildren().forEach(node -> {
            FadeTransition fade = new FadeTransition(Duration.millis(850), node);
            fade.setFromValue(0.35); fade.setToValue(1); fade.setAutoReverse(true);
            fade.setCycleCount(FadeTransition.INDEFINITE); fade.play();
        });
        ScaleTransition pulse = new ScaleTransition(Duration.millis(650), begin);
        pulse.setFromX(1); pulse.setFromY(1); pulse.setToX(1.045); pulse.setToY(1.045);
        pulse.setAutoReverse(true); pulse.setCycleCount(ScaleTransition.INDEFINITE); pulse.play();
    }

    private HBox controlRow(String key, String title, String detail) {
        Label keyLabel = label(key, 20, "#071B2E");
        keyLabel.setAlignment(Pos.CENTER);
        keyLabel.setMinWidth(125);
        keyLabel.setStyle("-fx-background-color:#67E4FF;-fx-background-radius:10;-fx-padding:12;-fx-font-weight:900;");
        VBox text = new VBox(4, label(title, 12, "#72E5FF"), label(detail, 12, "#C1D7E8"));
        HBox row = new HBox(18, keyLabel, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void startMatch(boolean isPractice) {
        stopTimer();
        practice = isPractice;
        finished = false;
        extraTime = false;
        remaining = 90;
        userGoals = 0; botGoals = 0;
        playerX = 155; playerY = FLOOR - 76; playerVY = 0;
        botX = 845; botY = FLOOR - 76; botVY = 0;
        resetBall();
        playerCooldown = botCooldown = botJumpCooldown = playerHeaderCooldown = botHeaderCooldown = elapsed = ballStuckTimer = 0;
        left = right = jump = shoot = false;
        aiLevel = isPractice ? 0 : importantMatch ? 3 : calculateAiLevel();
        aiDescription = isPractice ? "NORMAL · PRÁCTICA" : "LEYENDA · " + aiLabel();
        buildGameScene();
    }

    private int calculateAiLevel() {
        // Se disputan siempre los cinco cruces. La IA ajusta su presión al marcador.
        if (wins >= 2 && losses == 0) return 3; // 2-0: el tercer partido es el más exigente.
        if (wins >= 2 && losses >= 2) return 2; // 2-2: final un poco más difícil.
        if (wins >= 2 || (wins >= 1 && losses >= 1)) return 2;
        return 1;
    }

    private String aiLabel() {
        return aiLevel >= 3 ? "ÉLITE" : aiLevel == 2 ? "AVANZADA" : "BASE";
    }

    private void buildGameScene() {
        BorderPane root = shell();
        VBox top = new VBox(8);
        top.setPadding(new Insets(14, 22, 10, 22));
        HBox heading = new HBox(10);
        heading.setAlignment(Pos.CENTER_LEFT);
        statusLabel = label(practice ? "PARTIDO DE PRÁCTICA" : importantMatch ? importantMatchTitle : "DESAFÍO LEYENDA · " + (nextOpponent + 1) + "/5  VS  " + OPPONENTS[nextOpponent], 14, "#7CE8FF");
        scoreLabel = label("0  -  0", 27, "#F5FBFF");
        clockLabel = label("01:30", 20, "#72F0A4");
        Button music = button("♫  X MIL NOCHES", false);
        music.setOnAction(e -> openYoutubeMusic());
        Button exitMode = button("☰  SALIR DEL 1 VS 1", false);
        exitMode.setOnAction(e -> {
            stopTimer();
            left = right = jump = shoot = false;
            onExit.run();
        });
        heading.getChildren().addAll(statusLabel, spacer(), label(player.getNombre().toUpperCase(Locale.ROOT), 10, "#C7DCEC"), scoreLabel,
                label(practice ? "BOT" : importantMatch ? "RIVAL" : OPPONENTS[nextOpponent], 10, "#C7DCEC"), clockLabel);
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Label instruction = label("← → MOVER   ·   ↑ SALTAR   ·   ESPACIO DISPARAR", 11, "#8EABC3");
        toolbar.getChildren().addAll(instruction, spacer(), music, exitMode);
        top.getChildren().addAll(heading, toolbar);
        canvas = new Canvas(W, H);
        canvas.setManaged(false);
        StackPane arena = new StackPane();
        arena.setMinSize(0, 0);
        Runnable scaleArena = () -> {
            double availableW = Math.max(100, arena.getWidth());
            double availableH = Math.max(100, arena.getHeight());
            double scale = Math.min(availableW / W, availableH / H);
            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
            canvas.setLayoutX((availableW - W) / 2.0);
            canvas.setLayoutY((availableH - H) / 2.0);
        };
        arena.getChildren().add(canvas);
        arena.widthProperty().addListener((obs, oldValue, newValue) -> scaleArena.run());
        arena.heightProperty().addListener((obs, oldValue, newValue) -> scaleArena.run());
        root.setTop(top);
        root.setCenter(arena);
        Scene scene = new Scene(root, Math.max(720, stage.getWidth()), Math.max(520, stage.getHeight()));
        scene.getStylesheets().add(css());
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.F11) { stage.setFullScreen(!stage.isFullScreen()); e.consume(); return; }
            if (code == KeyCode.LEFT || code == KeyCode.A) left = true;
            if (code == KeyCode.RIGHT || code == KeyCode.D) right = true;
            if (code == KeyCode.UP || code == KeyCode.W) jump = true;
            if (code == KeyCode.SPACE) shoot = true;
        });
        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) left = false;
            if (code == KeyCode.RIGHT || code == KeyCode.D) right = false;
            if (code == KeyCode.UP || code == KeyCode.W) jump = false;
            if (code == KeyCode.SPACE) shoot = false;
        });
        stage.setScene(scene);
        stage.setTitle("LEYENDA | " + (practice ? "Práctica 1 vs 1" : importantMatch ? "Partido importante 1 vs 1" : "Desafío Leyenda"));
        scaleArena.run();
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        draw();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (lastFrame == 0) lastFrame = now;
                double dt = Math.min(0.035, (now - lastFrame) / 1_000_000_000.0);
                lastFrame = now;
                update(dt);
                draw();
            }
        };
        timer.start();
    }

    private void update(double dt) {
        if (finished) return;
        elapsed += dt;
        remaining -= dt;
        playerCooldown = Math.max(0, playerCooldown - dt);
        playerHeaderCooldown = Math.max(0, playerHeaderCooldown - dt);
        botCooldown = Math.max(0, botCooldown - dt);

        double speed = 245 + Math.max(0, player.getVelocidad() - 60) * 1.25;
        if (left) playerX -= speed * dt;
        if (right) playerX += speed * dt;
        playerX = clamp(playerX, 52, W - 52);
        if (jump && playerY >= FLOOR - 77) playerVY = -570;
        playerY += playerVY * dt;
        playerVY += 1450 * dt;
        if (playerY > FLOOR - 77) { playerY = FLOOR - 77; playerVY = 0; }

        updateBot(dt);
        updateBall(dt);
        if (finished) return;
        collidePlayers();
        if (shoot && playerCooldown <= 0) kickPlayer();
        if (remaining <= 0) {
            remaining = 0;
            if (userGoals == botGoals && !extraTime) {
                extraTime = true;
                remaining = 15; // desempate corto para que cada partido tenga un ganador.
                statusLabel.setText("TIEMPO EXTRA · GOL DE ORO");
            } else if (userGoals == botGoals) {
                // Si sigue empatado, se juega otro bloque de tiempo extra hasta el gol de oro.
                remaining = 15;
            } else finishMatch();
        }
        if (clockLabel != null) clockLabel.setText(String.format(Locale.ROOT, "%02d:%02d",
                (int) remaining / 60, (int) remaining % 60));
        if (scoreLabel != null) scoreLabel.setText(userGoals + "  -  " + botGoals);
    }

    private void updateBot(double dt) {
        botJumpCooldown = Math.max(0, botJumpCooldown - dt);
        botHeaderCooldown = Math.max(0, botHeaderCooldown - dt);
        double difficultySpeed = aiLevel == 0 ? 175 : aiLevel == 1 ? 235 : aiLevel == 2 ? 275 : 315;
        // El bot no persigue la pelota por encima del travesaño: vuelve a una posición defensiva.
        boolean ballPlayable = ballY > 95 && ballY < FLOOR - 12;
        double targetX = ballPlayable ? ballX + (ballX > botX ? -38 : 38) : W - 155;
        targetX = clamp(targetX, W * 0.55, W - 52);
        if (Math.abs(targetX - botX) > 12) botX += Math.signum(targetX - botX) * difficultySpeed * dt;
        botX = clamp(botX, 52, W - 52);

        // Salta solo una vez por intento y únicamente si la pelota está a una altura alcanzable.
        boolean reachableHeader = ballY > botY - 20 && ballY < botY + 45
                && Math.abs(ballX - botX) < 92;
        if (reachableHeader && botY >= FLOOR - 77 && botJumpCooldown <= 0) {
            botVY = -(aiLevel >= 2 ? 610 : 535);
            botJumpCooldown = 1.15;
        }
        botY += botVY * dt;
        botVY += 1450 * dt;
        if (botY > FLOOR - 77) { botY = FLOOR - 77; botVY = 0; }

        // Remate de bot exclusivamente desde el pie; nunca impulsa la pelota con el pecho/cuerpo.
        double footX = botX - 27;
        double footY = botY + 76;
        if (botCooldown <= 0 && Math.abs(ballX - footX) < 54 && Math.abs(ballY - footY) < 34) {
            double power = aiLevel == 0 ? 390 : aiLevel == 1 ? 460 : aiLevel == 2 ? 530 : 600;
            ballVX = -power;
            ballVY = -115 - aiLevel * 24;
            botCooldown = aiLevel == 0 ? 1.0 : aiLevel == 1 ? 0.72 : 0.5;
        }
    }

    private void updateBall(double dt) {
        double previousX = ballX;
        ballX += ballVX * dt;
        ballY += ballVY * dt;
        ballVY += 850 * dt;
        ballVX *= Math.pow(0.992, dt * 60);
        if (ballY > FLOOR - 18) { ballY = FLOOR - 18; ballVY = -Math.abs(ballVY) * 0.62; }
        if (Math.abs(ballVY) < 35 && ballY >= FLOOR - 19) ballVY = 0;

        final double goalTop = FLOOR - 150;
        final double radius = 13;
        // El gol se valida únicamente al cruzar la línea, con toda la pelota debajo del travesaño.
        boolean crossedLeft = previousX >= 42 && ballX < 42 && ballVX < 0;
        boolean crossedRight = previousX <= W - 42 && ballX > W - 42 && ballVX > 0;
        boolean insideGoalHeight = ballY - radius >= goalTop && ballY + radius <= FLOOR;
        if (crossedLeft && insideGoalHeight) {
            botGoals++;
            resetAfterGoal();
            return;
        }
        if (crossedRight && insideGoalHeight) {
            userGoals++;
            resetAfterGoal();
            return;
        }

        // Si la pelota llega por arriba, el travesaño la devuelve: no puede caer luego y contar como gol.
        // Travesaños blancos: si la pelota cruza por arriba, rebota hacia el campo y no puede
        // quedarse suspendida encima del arco ni caer luego detrás de la línea para contar gol.
        boolean overLeftGoal = ballX >= 0 && ballX <= 48 && ballY + radius >= goalTop - 2
                && ballY < goalTop + 24;
        boolean overRightGoal = ballX <= W && ballX >= W - 48 && ballY + radius >= goalTop - 2
                && ballY < goalTop + 24;
        if (overLeftGoal || overRightGoal) {
            ballY = goalTop - radius - 2;
            ballVY = -Math.max(190, Math.abs(ballVY) * 0.72);
            ballVX = overLeftGoal ? Math.max(150, Math.abs(ballVX) * 0.65) : -Math.max(150, Math.abs(ballVX) * 0.65);
        }
        // Detección de pelota casi inmóvil en la zona alta: la devuelve al terreno de juego.
        if (ballY < goalTop && Math.abs(ballVX) < 18 && Math.abs(ballVY) < 45) ballStuckTimer += dt;
        else ballStuckTimer = 0;
        if (ballStuckTimer > 0.85) {
            ballY = Math.max(goalTop - 20, ballY);
            ballVX = ballX < W / 2 ? 185 : -185;
            ballVY = 230;
            ballStuckTimer = 0;
        }
        // Red de seguridad: si por una velocidad extrema o un frame perdido la pelota
        // atraviesa los límites, se repone en el centro en lugar de desaparecer.
        if (ballX < -radius - 24 || ballX > W + radius + 24
                || ballY < -radius - 24 || ballY > H + radius + 24
                || !Double.isFinite(ballX) || !Double.isFinite(ballY)) {
            resetBall();
            return;
        }
        if (ballX < 18 || ballX > W - 18) {
            ballX = clamp(ballX, 18, W - 18);
            ballVX *= -0.75;
        }
        if (ballY < 18) { ballY = 18; ballVY = Math.abs(ballVY) * 0.7; }
    }

    private void collidePlayers() {
        // El cuerpo no dispara ni empuja la pelota: solo se permiten remates con el pie.
        // El cabezazo es un contacto separado, habilitado por el atributo entrenado.
        int header = headerSkill();
        if (header >= 65) {
            double headerRadius = 15 + (header - 65) * 0.18;
            if (playerHeaderCooldown <= 0 && Math.abs(ballX - playerX) < 27 && ballY < playerY + 16
                    && ballY > playerY - headerRadius) {
                ballVX = Math.max(ballVX, 0) + 65 + header * 0.9;
                ballVY = -Math.max(150, 115 + header * 1.8);
                playerHeaderCooldown = 0.55;
            }
        }
        // La IA puede cabecear con una calidad media, sin convertir el contacto corporal en tiro.
        if (botHeaderCooldown <= 0 && Math.abs(ballX - botX) < 25 && ballY < botY + 16 && ballY > botY - 12) {
            ballVX = Math.min(ballVX, 0) - 85;
            ballVY = -190;
            botHeaderCooldown = 0.55;
        }
    }

    private int headerSkill() {
        if (player instanceof Delantero d) return d.getCabezazo();
        if (player instanceof Defensa d) return d.getCabezazo();
        return 0;
    }

    private void kickPlayer() {
        // El remate sale desde el pie adelantado, no desde la cabeza.
        if (Math.abs(ballX - (playerX + 28)) < 58 && Math.abs(ballY - (playerY + 76)) < 34) {
            double power = 430 + Math.max(0, player.getRemate() - 60) * 3.2
                    + Math.max(0, player.getFuerza() - 60) * 1.2;
            ballVX = power;
            ballVY = -95 - Math.max(0, player.getDefinicion() - 60) * 1.25;
            playerCooldown = 0.42;
        }
    }

    private void resetAfterGoal() {
        resetBall();
        playerX = 155; botX = 845;
        playerY = botY = FLOOR - 77;
        playerVY = botVY = 0;
        if (scoreLabel != null) scoreLabel.setText(userGoals + "  -  " + botGoals);
        if (extraTime) finishMatch(); // gol de oro.
    }

    private void resetBall() {
        ballX = W / 2; ballY = FLOOR - 30;
        ballVX = ballVY = 0;
    }

    private void finishMatch() {
        finished = true;
        stopTimer();
        if (practice) {
            practiceFinished = true;
            showPracticeResult();
            return;
        }
        boolean won = userGoals > botGoals;
        if (importantMatch) {
            if (importantMatchResult != null) importantMatchResult.accept(won);
            showImportantMatchResult(won);
            return;
        }
        if (won) wins++; else losses++;
        matchLog.add(OPPONENTS[nextOpponent] + "  " + userGoals + " - " + botGoals + "  " + (won ? "VICTORIA" : "DERROTA"));
        nextOpponent++;
        showMatchResult(won);
    }

    private void showImportantMatchResult(boolean won) {
        BorderPane root = shell();
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(38));
        boolean finalMatch = importantMatchTitle.toUpperCase(Locale.ROOT).contains("FINAL");
        String headline = finalMatch ? (won ? "¡FELICIDADES, CAMPEÓN!" : "¡SUBCAMPEÓN! EN OTRO AÑO SERÁ")
                : (won ? "¡PARTIDO GANADO!" : "PARTIDO PERDIDO");
        String detail = finalMatch ? (won ? "Ganaste la final y levantaste el trofeo. Tu logro quedó registrado." : "Llegaste a la final y terminaste subcampeón. En otro año será.")
                : (won ? "La victoria quedó registrada en tu historial." : "El resultado quedó registrado en tu historial.");
        box.getChildren().addAll(
                label(headline, 20, won ? "#75F0A4" : "#FFB477"),
                label(userGoals + "  -  " + botGoals, 50, "#F4FBFF"),
                label(importantMatchTitle, 22, "#8BEAFF"),
                label(detail, 14, "#BBD6E8"));
        Button back = button("VOLVER A COMPETICIONES  →", true);
        back.setOnAction(e -> onExit.run());
        box.getChildren().add(back);
        root.setCenter(box);
        setScene(root);
    }

    private void showPracticeResult() {
        BorderPane root = shell();
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER); box.setPadding(new Insets(40));
        box.getChildren().addAll(label("PRÁCTICA COMPLETADA", 13, "#6CE5FF"),
                label("¡YA ESTÁS LISTO!", 34, "#F4FBFF"),
                label("Resultado de entrenamiento: " + userGoals + " - " + botGoals, 17, "#BBD6E8"),
                label("Ahora comienza el desafío oficial. Vas a enfrentar a Racing, San Lorenzo, Independiente, River y Boca.", 14, "#BBD6E8"));
        Button go = button("IR AL DESAFÍO LEYENDA  →", true);
        go.setOnAction(e -> { nextOpponent = 0; wins = losses = 0; matchLog.clear(); showMatchIntro(); });
        box.getChildren().add(go); root.setCenter(box);
        setScene(root);
    }

    private void showMatchIntro() {
        if (nextOpponent >= 5) { showTournamentSummary(); return; }
        int level = calculateAiLevel();
        BorderPane root = shell();
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER); box.setPadding(new Insets(35));
        box.getChildren().addAll(label("PARTIDO " + (nextOpponent + 1) + " DE 5  ·  DIFICULTAD LEYENDA", 12, "#6CE5FF"),
                label("TU EQUIPO: " + club.toUpperCase(Locale.ROOT), 14, "#B9D7E9"),
                label("VS", 13, "#6CE5FF"),
                label(OPPONENTS[nextOpponent], 36, "#F4FAFF"),
                label("MARCADOR DE LA SERIE   " + wins + " VICTORIAS  ·  " + losses + " DERROTAS", 14, "#75E9A7"),
                label("IA: " + (level >= 3 ? "ÉLITE — PRESIÓN MÁXIMA" : level == 2 ? "AVANZADA — PRESIÓN ALTA" : "LEYENDA — INTENSA"), 13, "#FFCF75"),
                label(level >= 3 ? "El rival ajustó su juego: reacciona más rápido, salta mejor y remata con más potencia." :
                        "Partido de 90 segundos. Necesitás 3 victorias para consagrarte.", 13, "#B9D7E9"));
        Button play = button("JUGAR PARTIDO  →", true);
        play.setOnAction(e -> startMatch(false));
        box.getChildren().add(play); root.setCenter(box);
        setScene(root);
    }

    private void showMatchResult(boolean won) {
        BorderPane root = shell();
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER); box.setPadding(new Insets(35));
        box.getChildren().addAll(label(won ? "¡VICTORIA!" : "PARTIDO FINALIZADO", 13, won ? "#75F0A4" : "#FFB477"),
                label(userGoals + "  -  " + botGoals, 48, "#F4FBFF"),
                label(OPPONENTS[nextOpponent - 1], 20, "#8BEAFF"),
                label("SERIE: " + wins + " GANADOS  ·  " + losses + " PERDIDOS", 15, "#BBD6E8"));
        String detail = nextOpponent >= 5 ? (wins >= 3
                ? "¡Ganaste al menos 3 de los 5 partidos y completaste el desafío!"
                : "No alcanzaste las 3 victorias. Podés volver a intentarlo.")
                : "Siguiente rival: " + OPPONENTS[nextOpponent];
        box.getChildren().add(label(detail, 14, "#BBD6E8"));
        Button next = button(nextOpponent >= 5 ? "VER RESULTADO FINAL" : "CONTINUAR  →", true);
        next.setOnAction(e -> showMatchIntro());
        box.getChildren().add(next); root.setCenter(box);
        setScene(root);
    }

    private void showTournamentSummary() {
        BorderPane root = shell();
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER); box.setPadding(new Insets(32));
        boolean champion = wins >= 3;
        box.getChildren().addAll(label("DESAFÍO FINAL · LEYENDA", 12, "#6CE5FF"),
                label(champion ? "¡SOS UNA LEYENDA!" : "FIN DEL DESAFÍO", 36, champion ? "#75F0A4" : "#F4FAFF"),
                label(player.getNombre() + " · " + player.getOvr() + " OVR · " + club, 14, "#BBD6E8"),
                label(wins + " VICTORIAS  ·  " + losses + " DERROTAS", 22, "#79E8FF"));
        VBox results = new VBox(8); results.setMaxWidth(560);
        for (String result : matchLog) results.getChildren().add(label(result, 13, "#C2D8E8"));
        box.getChildren().add(results);
        Button back = button("VOLVER AL LEGADO  →", true);
        back.setOnAction(e -> onExit.run());
        box.getChildren().add(back);
        root.setCenter(box); setScene(root);
    }

    private void draw() {
        if (canvas == null) return;
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#061326")); g.fillRect(0, 0, W, H);
        g.setFill(Color.web("#0B2944")); g.fillRoundRect(12, 12, W - 24, H - 24, 22, 22);
        drawStadium(g);
        g.setStroke(Color.web("#2A6685")); g.setLineWidth(3);
        g.strokeRoundRect(28, 28, W - 56, H - 56, 18, 18);
        g.strokeLine(W / 2, 28, W / 2, FLOOR);
        g.strokeOval(W / 2 - 75, FLOOR - 75, 150, 150);
        g.setFill(Color.web("#0E513D")); g.fillRect(28, FLOOR, W - 56, 42);
        g.setStroke(Color.web("#6CE8A5")); g.setLineWidth(4);
        g.strokeRect(0, FLOOR - 150, 42, 150);
        g.strokeRect(W - 42, FLOOR - 150, 42, 150);
        // Postes y travesaños blancos con red visible.
        g.setStroke(Color.WHITE); g.setLineWidth(7);
        g.strokeLine(4, FLOOR - 150, 42, FLOOR - 150); g.strokeLine(42, FLOOR - 150, 42, FLOOR);
        g.strokeLine(W - 42, FLOOR - 150, W - 4, FLOOR - 150); g.strokeLine(W - 42, FLOOR - 150, W - 42, FLOOR);
        g.setStroke(Color.color(1, 1, 1, 0.48)); g.setLineWidth(1);
        for (int y = (int)(FLOOR - 140); y < FLOOR; y += 14) {
            g.strokeLine(5, y, 40, y); g.strokeLine(W - 40, y, W - 5, y);
        }
        for (int x = 8; x < 42; x += 9) g.strokeLine(x, FLOOR - 148, x, FLOOR - 2);
        for (int x = (int)W - 42; x < W - 5; x += 9) g.strokeLine(x, FLOOR - 148, x, FLOOR - 2);
        // Líneas de área y arcos.
        g.setStroke(Color.web("#6EAB94")); g.setLineWidth(2);
        g.strokeRect(42, FLOOR - 125, 105, 125);
        g.strokeRect(W - 147, FLOOR - 125, 105, 125);
        drawPlayer(g, playerX, playerY, true);
        drawPlayer(g, botX, botY, false);
        if (ballImage != null && !ballImage.isError()) {
            g.drawImage(ballImage, ballX - 15, ballY - 15, 30, 30);
        } else {
            g.setFill(Color.WHITE); g.fillOval(ballX - 13, ballY - 13, 26, 26);
            g.setStroke(Color.web("#25384A")); g.setLineWidth(1.5); g.strokeOval(ballX - 13, ballY - 13, 26, 26);
        }
        g.setFill(Color.web("#D7E9F5")); g.setFont(Font.font("Arial", 12));
        g.fillText(player.getNombre().toUpperCase(Locale.ROOT), Math.max(35, playerX - 55), playerY - 14);
        g.fillText(practice ? "BOT" : importantMatch ? "RIVAL" : SHORT_NAMES[nextOpponent], Math.max(35, botX - 30), botY - 14);
        g.setFill(Color.web("#FFFFFF")); g.setFont(Font.font("Arial", 14));
        g.fillText("LEYENDA", W / 2 - 32, 55);
    }

    /** Fondo de tribunas del estadio. Se puede personalizar en este método. */
    private void drawStadium(GraphicsContext g) {
        g.setFill(Color.web("#132D49"));
        g.fillRoundRect(30, 30, W - 60, 205, 14, 14);
        g.setFill(Color.web("#203F5D"));
        g.fillRect(30, 55, W - 60, 34);
        g.fillRect(30, 105, W - 60, 34);
        g.fillRect(30, 155, W - 60, 34);
        // Público estilizado: pequeños puntos en filas para dar profundidad a las tribunas.
        Color[] crowd = {Color.web("#D6E4EE"), Color.web("#E6B95E"), Color.web("#E66C72"),
                Color.web("#65C6E8"), Color.web("#8BD3A5")};
        for (int row = 0; row < 4; row++) {
            double y = 45 + row * 43;
            for (int col = 0; col < 92; col++) {
                double x = 43 + col * 10.0;
                g.setFill(crowd[(col * 3 + row * 7) % crowd.length]);
                g.fillOval(x, y + ((col % 2) * 3), 4, 7);
            }
        }
        g.setFill(Color.web("#E8F1F7"));
        g.setFont(Font.font("Arial", 10));
        g.fillText("LEYENDA STADIUM", W / 2 - 48, 215);
        g.setFill(Color.web("#0D513B"));
        g.fillRect(28, 245, W - 56, FLOOR - 245);
        g.setFill(Color.web("#0F5B40"));
        for (int i = 0; i < 8; i++) g.fillRect(28 + i * 118, 245, 58, FLOOR - 245);
    }

    private void drawPlayer(GraphicsContext g, double x, double y, boolean human) {
        Color shirt = human ? Color.web("#45C8EF") : Color.web("#E74C5A");
        Color skin = Color.web("#E9B88D");
        double run = (human ? (left || right) : Math.abs(ballX - botX) > 20) ? Math.sin(elapsed * 13) * 8 : 0;
        g.setFill(Color.color(0, 0, 0, 0.25)); g.fillOval(x - 28, FLOOR - 7, 56, 12);
        // Cabeza y torso.
        g.setFill(skin); g.fillOval(x - 17, y, 34, 34);
        g.setFill(human ? Color.web("#163B61") : Color.web("#292D38"));
        g.fillArc(x - 18, y - 4, 36, 22, 0, 180, javafx.scene.shape.ArcType.ROUND);
        g.setFill(shirt); g.fillRoundRect(x - 23, y + 31, 46, 35, 9, 9);
        g.setFill(Color.WHITE); g.setFont(Font.font("Arial", 10));
        g.fillText(human ? String.valueOf(player.getDorsal()) : "10", x - 4, y + 53);
        // Piernas articuladas y botines visibles: el disparo se produce desde aquí.
        g.setStroke(Color.web("#E9B88D")); g.setLineWidth(7);
        g.strokeLine(x - 12, y + 60, x - 15 - run, y + 70);
        g.strokeLine(x + 12, y + 60, x + 19 + run, y + 70);
        g.setStroke(Color.web("#DCEAF4")); g.setLineWidth(6);
        g.strokeLine(x - 15 - run, y + 70, x - 20 - run, y + 76);
        g.strokeLine(x + 19 + run, y + 70, x + 24 + run, y + 76);
        g.setStroke(Color.web("#182638")); g.setLineWidth(7);
        g.strokeLine(x - 20 - run, y + 76, x - 9 - run, y + 76);
        g.strokeLine(x + 24 + run, y + 76, x + 35 + run, y + 76);
    }

    /** Abre la búsqueda de la canción; el volumen se controla desde el reproductor de YouTube. */
    private void openYoutubeMusic() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create("https://www.youtube.com/results?search_query=X+Mil+Noches"));
            }
        } catch (Exception ignored) {
            if (statusLabel != null) statusLabel.setText("No se pudo abrir YouTube en este dispositivo.");
        }
    }

    private Image loadBallImage() {
        try {
            String source = BALL_IMAGE_URL == null ? "" : BALL_IMAGE_URL.trim();
            if (source.isEmpty()) {
                var resource = getClass().getResource("/images/ball-2026.png");
                return resource == null ? null : new Image(resource.toExternalForm(), true);
            }
            return new Image(source, true);
        } catch (Exception ignored) { return null; }
    }

    private BorderPane shell() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#061326;");
        return root;
    }

    private VBox controlPanel() { return new VBox(); }

    private Label label(String text, int size, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI';-fx-font-size:" + size + "px;-fx-font-weight:900;-fx-text-fill:" + color + ";");
        return label;
    }

    private Button button(String text, boolean primary) {
        Button b = new Button(text);
        b.setStyle(primary
                ? "-fx-background-color:linear-gradient(to right,#6CE7FF,#34BFEA);-fx-text-fill:#05213A;-fx-font-weight:900;-fx-padding:14 22;-fx-background-radius:9;-fx-cursor:hand;"
                : "-fx-background-color:#143453;-fx-text-fill:#D9F3FF;-fx-font-weight:900;-fx-padding:14 22;-fx-background-radius:9;-fx-cursor:hand;");
        return b;
    }

    private HBox spacer() {
        HBox box = new HBox();
        javafx.scene.layout.HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);
        return box;
    }

    private String cardStyle() {
        return "-fx-background-color:#0D223D;-fx-background-radius:15;-fx-border-color:#1A3C5D;-fx-border-radius:15;";
    }

    private String css() {
        var resource = getClass().getResource("/styles/leyenda.css");
        return resource == null ? "" : resource.toExternalForm();
    }

    private void setScene(BorderPane root) {
        Scene scene = new Scene(root, Math.max(720, stage.getWidth()), Math.max(520, stage.getHeight()));
        scene.getStylesheets().add(css());
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) { stage.setFullScreen(!stage.isFullScreen()); e.consume(); }
        });
        stage.setScene(scene);
        stage.show();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void stopTimer() {
        if (timer != null) { timer.stop(); timer = null; }
        lastFrame = 0;
    }
}
