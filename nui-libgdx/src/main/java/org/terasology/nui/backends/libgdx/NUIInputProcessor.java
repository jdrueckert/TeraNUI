/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.nui.backends.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import org.terasology.input.ButtonState;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;
import org.terasology.input.device.KeyboardAction;
import org.terasology.input.device.MouseAction;

import java.util.LinkedList;
import java.util.Queue;

public class NUIInputProcessor implements InputProcessor {
    private Keyboard.Key lastKey;
    private Queue<KeyboardAction> keyboardActionQueue = new LinkedList<>();
    private Queue<MouseAction> mouseActionQueue = new LinkedList<>();
    private static NUIInputProcessor instance = new NUIInputProcessor();

    public static boolean CONSUME_INPUT = false;

    public NUIInputProcessor() {
    }

    public static void init() {
        InputMultiplexer inputMultiplexer;

        if (Gdx.input.getInputProcessor() instanceof InputMultiplexer) {
            inputMultiplexer = (InputMultiplexer)Gdx.input.getInputProcessor();
        } else {
            InputProcessor currentProcessor = Gdx.input.getInputProcessor();
            if (currentProcessor == null) {
                inputMultiplexer = new InputMultiplexer();
            } else {
                inputMultiplexer = new InputMultiplexer(currentProcessor);
            }
            Gdx.input.setInputProcessor(inputMultiplexer);
        }

        if (!inputMultiplexer.getProcessors().contains(instance, true)) {
            inputMultiplexer.addProcessor(0, instance);
        }
    }

    public static NUIInputProcessor getInstance() {
        return instance;
    }

    public Queue<KeyboardAction> getKeyboardInputQueue() {
        Queue<KeyboardAction> copy = new LinkedList<>(keyboardActionQueue);
        keyboardActionQueue.clear();
        return copy;
    }

    public Queue<MouseAction> getMouseInputQueue() {
        Queue<MouseAction> copy = new LinkedList<>(mouseActionQueue);
        mouseActionQueue.clear();
        return copy;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Input.Keys.toString(keycode).equalsIgnoreCase("UNKNOWN")) {
            return false;
        }
        Keyboard.Key key = GDXInputUtil.GDXToTerasologyKey(keycode);
        char keyChar = GDXInputUtil.getGDXKeyChar(keycode);
        lastKey = key;
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) || keyChar == '\n') {
            // NOTE: Control+Key combinations do not produce valid key chars (fixes input field bugs)
            keyChar = 0;
        } else if (keyChar != 0 && keyChar != '\t') {
            return false;
        }

        keyboardActionQueue.add(new KeyboardAction(key, ButtonState.DOWN, keyChar));
        return CONSUME_INPUT;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (Input.Keys.toString(keycode).equalsIgnoreCase("UNKNOWN")) {
            return false;
        }

        Keyboard.Key key = GDXInputUtil.GDXToTerasologyKey(keycode);
        char keyChar = GDXInputUtil.getGDXKeyChar(keycode);
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            // NOTE: Control+Key combinations do not produce valid key chars (fixes input field bugs)
            keyChar = 0;
        } else if (keyChar != 0) {
            return false;
        }

        keyboardActionQueue.add(new KeyboardAction(key, ButtonState.UP, keyChar));
        return CONSUME_INPUT;
    }

    @Override
    public boolean keyTyped(char character) {
        if (Character.isISOControl(character) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            return false;
        }

        // HACK: Backslash character is not identified in keyDown and keyUp events
        if (character == '\\') {
            lastKey = Keyboard.Key.BACKSLASH;
        }

        keyboardActionQueue.add(new KeyboardAction(lastKey, ButtonState.DOWN, character));
        keyboardActionQueue.add(new KeyboardAction(lastKey, ButtonState.UP, character));
        return CONSUME_INPUT;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseActionQueue.add(new MouseAction(GDXInputUtil.GDXToTerasologyMouseButton(button), ButtonState.DOWN, GDXInputUtil.GDXToNUIMousePosition(screenX, screenY)));
        return CONSUME_INPUT;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseActionQueue.add(new MouseAction(GDXInputUtil.GDXToTerasologyMouseButton(button), ButtonState.UP, GDXInputUtil.GDXToNUIMousePosition(screenX, screenY)));
        return CONSUME_INPUT;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        amount = (amount < 0 ? 1 : -1);
        mouseActionQueue.add(new MouseAction(InputType.MOUSE_WHEEL.getInput(amount), amount, GDXInputUtil.GDXToNUIMousePosition(Gdx.input.getX(), Gdx.input.getY())));
        return CONSUME_INPUT;
    }
}
