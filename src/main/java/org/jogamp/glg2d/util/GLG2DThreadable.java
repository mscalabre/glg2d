/*
 * Copyright 2019 matth.
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
package org.jogamp.glg2d.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author matth
 */
public interface GLG2DThreadable {
    public Future<Object> run(Runnable runnable);
    public void runOrReplace(String key, Runnable runnable);
    public void runAfterLock(String key, Runnable runnable);
    public void setRender(boolean b);
    public boolean isRender();
}
