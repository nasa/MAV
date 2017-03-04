/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.arc.atc.algos;

import java.io.*;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FirstLineSequentialVsParallelService extends Application {
    private static final String[] URLS = {
            "http://www.google.com",
            "http://www.yahoo.com",
            "http://www.microsoft.com",
            "http://www.oracle.com"
    };

    private ExecutorService sequentialFirstLineExecutor;
    private ExecutorService parallelFirstLineExecutor;

    @Override
    public void init() throws Exception {
        sequentialFirstLineExecutor = Executors.newFixedThreadPool(
                1,
                new FirstLineThreadFactory("sequential")
        );

        parallelFirstLineExecutor = Executors.newFixedThreadPool(
                URLS.length,
                new FirstLineThreadFactory("parallel")
        );
    }

    @Override
    public void stop() throws Exception {
        parallelFirstLineExecutor.shutdown();
        parallelFirstLineExecutor.awaitTermination(3, TimeUnit.SECONDS);

        sequentialFirstLineExecutor.shutdown();
        sequentialFirstLineExecutor.awaitTermination(3, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final VBox messages = new VBox();
        messages.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");

        messages.getChildren().addAll(
                new Label("Parallel Execution"),
                new Label("------------------"),
                new Label()
        );
        fetchFirstLines(messages, parallelFirstLineExecutor);

        messages.getChildren().addAll(
                new Label("Sequential Execution"),
                new Label("--------------------"),
                new Label()
        );
        fetchFirstLines(messages, sequentialFirstLineExecutor);

        messages.setStyle("-fx-font-family: monospace");

        stage.setScene(new Scene(messages, 600, 800));
        stage.show();
    }

    private void fetchFirstLines(final VBox monitoredLabels, ExecutorService executorService) {
        for (final String url : URLS) {
            final FirstLineService service = new FirstLineService();
            service.setExecutor(executorService);
            service.setUrl(url);

            final ProgressMonitoredLabel monitoredLabel = new ProgressMonitoredLabel(url);
            monitoredLabels.getChildren().add(monitoredLabel);
            monitoredLabel.progress.progressProperty().bind(service.progressProperty());

            service.setOnSucceeded((WorkerStateEvent t) ->
                    monitoredLabel.addStrings(
                            service.getMessage(),
                            service.getValue()
                    )
            );
            service.start();
        }
    }

    public class ProgressMonitoredLabel extends HBox {
        final ProgressBar progress;
        final VBox labels;

        public ProgressMonitoredLabel(String initialString) {
            super(20);

            progress = new ProgressBar();
            labels = new VBox();
            labels.getChildren().addAll(new Label(initialString), new Label());

            progress.setPrefWidth(100);
            progress.setMinWidth(ProgressBar.USE_PREF_SIZE);
            HBox.setHgrow(labels, Priority.ALWAYS);
            setMinHeight(80);

            getChildren().addAll(progress, labels);
        }

        void addStrings(String... strings) {
            for (String string : strings) {
                labels.getChildren().add(
                        labels.getChildren().size() - 1,
                        new Label(string)
                );
            }
        }
    }

    public static class FirstLineService extends Service<String> {
        private StringProperty url = new SimpleStringProperty(this, "url");

        final void setUrl(String value) {
            url.set(value);
        }

        final String getUrl() {
            return url.get();
        }

//        public final StringProperty urlProperty() {
//            return url;
//        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        protected Task createTask() {
            final String _url = getUrl();
            return new Task<String>() {
                protected String call() throws Exception {
                    updateMessage("Called on thread: " + Thread.currentThread().getName());
                    URL u = new URL(_url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(u.openStream()));
                    String result = in.readLine();
                    in.close();

                    // pause just so that it really takes some time to run the task
                    // so that parallel execution behaviour can be observed.
                    for (int i = 0; i < 100; i++) {
                        updateProgress(i, 100);
                        Thread.sleep(50);
                    }

                    return result;
                }
            };
        }
    }

    static class FirstLineThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final String type;

        public FirstLineThreadFactory(String type) {
            this.type = type;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "LineService-" + poolNumber.getAndIncrement() + "-thread-" + type);
            thread.setDaemon(true);

            return thread;
        }
    }
}
