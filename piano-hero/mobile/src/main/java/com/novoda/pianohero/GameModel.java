package com.novoda.pianohero;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final Piano piano;
    private final SongPlayer songPlayer;
    private final Clickable startGameClickable;
    private final ViewModelConverter converter;
    private final GameTimer gameTimer;

    private Score score = Score.initial();

    GameModel(
            SongSequenceFactory songSequenceFactory,
            Piano piano,
            Clickable startGameClickable,
            GameTimer gameTimer,
            ViewModelConverter converter,
            SongPlayer songPlayer
    ) {
        this.songSequenceFactory = songSequenceFactory;
        this.piano = piano;
        this.startGameClickable = startGameClickable;
        this.gameTimer = gameTimer;
        this.converter = converter;
        this.songPlayer = songPlayer;
    }

    @Override
    public void startGame(final GameCallback gameCallback) {
        startGameClickable.setListener(new Clickable.Listener() {
            @Override
            public void onClick() {
                emitInitialGameState(gameCallback);
            }
        });

        songPlayer.attachListeners(new SongPlayer.SongGameCallback() {
            @Override
            public void onStartPlayingNote(Note note, Sequence sequence) {
                GameInProgressViewModel gameInProgressViewModel = converter.createCurrentlyPressingNoteGameInProgressViewModel(note, sequence, score);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onCorrectNotePlayed(Sequence sequence) {
                score = score.increment();
                GameInProgressViewModel gameInProgressViewModel = converter.createCorrectNotePressedGameInProgressViewModel(sequence, score);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onIncorrectNotePlayed(Sequence sequence) {
                score = score.decrement();
                GameInProgressViewModel gameInProgressViewModel = converter.createIncorrectNotePressedGameInProgressViewModel(sequence, score);
                gameCallback.onGameProgressing(gameInProgressViewModel);
            }

            @Override
            public void onFinalNoteInSequencePlayedSuccessfully() {
                gameCallback.onSongComplete();

                Sequence sequence = songSequenceFactory.maryHadALittleLamb();
                songPlayer.loadSong(sequence);
            }

        });
        piano.attachListener(songPlayingNoteListener);

        gameTimer.start(new GameTimer.Callback() {
            @Override
            public void onSecondTick(long secondsUntilFinished) {
                ClockViewModel clockViewModel = converter.createClockViewModel(secondsUntilFinished);
                gameCallback.onClockTick(clockViewModel);
            }

            @Override
            public void onFinish() {
                gameCallback.onGameComplete(converter.createGameOverViewModel(score));
            }
        });

        emitInitialGameState(gameCallback);
    }

    private void emitInitialGameState(GameCallback gameCallback) {
        this.score = Score.initial();
        Sequence sequence = songSequenceFactory.maryHadALittleLamb();
        songPlayer.loadSong(sequence);

        GameInProgressViewModel gameInProgressViewModel = converter.createStartGameInProgressViewModel(sequence, score);
        gameCallback.onGameProgressing(gameInProgressViewModel);
    }

    private final Piano.NoteListener songPlayingNoteListener = new Piano.NoteListener() {
        @Override
        public void onStart(Note note) {
            if (gameTimer.gameInProgress()) {
                songPlayer.onStartPlaying(note);
            }
        }

        @Override
        public void onStop(Note note) {
            if (gameTimer.gameInProgress()) {
                songPlayer.onStopPlaying(note);
            }
        }
    };

    @Override
    public void stopGame() {
        gameTimer.stop();
    }

}
