/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2015 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.uifx.views.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import jgnash.engine.Engine;
import jgnash.engine.EngineFactory;
import jgnash.engine.message.Message;
import jgnash.engine.message.MessageBus;
import jgnash.engine.message.MessageChannel;
import jgnash.engine.message.MessageListener;
import jgnash.uifx.StaticUIMethods;
import jgnash.uifx.tasks.CloseFileTask;

/**
 * @author Craig Cavanaugh
 */
public class MainToolBarController implements MessageListener {

    @FXML
    private Button closeButton;

    @FXML
    private Button updateCurrencies;

    @FXML
    private Button updateSecurities;

    @FXML
    private void initialize() {
        MessageBus.getInstance().registerListener(this, MessageChannel.SYSTEM);
    }

    @FXML
    private void handleOpenAction() {
        StaticUIMethods.showOpenDialog();
    }

    @FXML
    private void handleCloseAction() {
        if (EngineFactory.getEngine(EngineFactory.DEFAULT) != null) {
            CloseFileTask.initiateShutdown();
        }
    }

    @Override
    public void messagePosted(final Message event) {

        Platform.runLater(() -> {
            switch (event.getEvent()) {
                case FILE_NEW_SUCCESS:
                case FILE_LOAD_SUCCESS:
                    updateSecurities.setDisable(false);
                    updateCurrencies.setDisable(false);
                    closeButton.setDisable(false);
                    break;
                case FILE_CLOSING:
                case FILE_LOAD_FAILED:
                    updateSecurities.setDisable(true);
                    updateCurrencies.setDisable(true);
                    closeButton.setDisable(true);
                    break;
                default:
                    break;
            }
        });
    }

    @FXML
    private void handleSecuritiesUpdateAction() {
        final Engine engine = EngineFactory.getEngine(EngineFactory.DEFAULT);
        if (engine != null) {
            engine.startSecuritiesUpdate(0);
        }
    }

    @FXML
    private void handleCurrenciesUpdateAction() {
        final Engine engine = EngineFactory.getEngine(EngineFactory.DEFAULT);
        if (engine != null) {
            engine.startExchangeRateUpdate(0);
        }
    }
}
