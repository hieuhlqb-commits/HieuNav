package com.hieuhlqb.hieunav;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Surface;

import androidx.car.app.AppManager;
import androidx.car.app.CarAppService;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.SurfaceCallback;
import androidx.car.app.SurfaceContainer;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.navigation.model.NavigationTemplate;
import androidx.car.app.validation.HostValidator;

/**
 * Android Auto entry point for HieuNav.
 *
 * This first integration establishes the official navigation-app service and
 * the car-safe NavigationTemplate. The map renderer is an intentionally simple
 * placeholder until the phone map and route engine are shared with this surface.
 */
public final class HieuNavCarAppService extends CarAppService {

    @Override
    public HostValidator createHostValidator() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        }
        return new HostValidator.Builder(this)
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build();
    }

    @Override
    public Session onCreateSession() {
        return new HieuNavCarSession();
    }

    private static final class HieuNavCarSession extends Session {
        @Override
        public Screen onCreateScreen(Intent intent) {
            return new HieuNavCarScreen(getCarContext());
        }
    }

    private static final class HieuNavCarScreen extends Screen {
        private Surface currentSurface;

        HieuNavCarScreen(CarContext carContext) {
            super(carContext);
            getCarContext().getCarService(AppManager.class)
                    .setSurfaceCallback(new HieuNavSurfaceCallback());
        }

        @Override
        public androidx.car.app.model.Template onGetTemplate() {
            Action statusAction = new Action.Builder()
                    .setTitle("HieuNav")
                    .setOnClickListener(() -> getCarContext()
                            .getCarService(AppManager.class)
                            .showToast("HieuNav đã kết nối Android Auto",
                                    androidx.car.app.CarToast.LENGTH_SHORT))
                    .build();

            ActionStrip actions = new ActionStrip.Builder()
                    .addAction(statusAction)
                    .build();

            return new NavigationTemplate.Builder()
                    .setActionStrip(actions)
                    .build();
        }

        private final class HieuNavSurfaceCallback implements SurfaceCallback {
            @Override
            public void onSurfaceAvailable(SurfaceContainer container) {
                currentSurface = container.getSurface();
                drawWaitingMap(currentSurface, container.getWidth(), container.getHeight());
            }

            @Override
            public void onSurfaceDestroyed(SurfaceContainer container) {
                if (currentSurface != null) {
                    currentSurface.release();
                    currentSurface = null;
                }
            }
        }

        private void drawWaitingMap(Surface surface, int width, int height) {
            if (surface == null || !surface.isValid()) return;
            Canvas canvas = null;
            try {
                canvas = surface.lockCanvas(null);
                if (canvas == null) return;

                canvas.drawColor(Color.rgb(24, 31, 38));
                Paint road = new Paint(Paint.ANTI_ALIAS_FLAG);
                road.setColor(Color.rgb(64, 78, 88));
                road.setStyle(Paint.Style.STROKE);
                road.setStrokeWidth(Math.max(5f, width * 0.012f));

                Path roads = new Path();
                roads.moveTo(width * 0.08f, height * 0.82f);
                roads.cubicTo(width * 0.30f, height * 0.72f,
                        width * 0.36f, height * 0.36f,
                        width * 0.62f, height * 0.30f);
                roads.cubicTo(width * 0.78f, height * 0.25f,
                        width * 0.84f, height * 0.12f,
                        width * 0.96f, height * 0.08f);
                canvas.drawPath(roads, road);

                Path crossRoad = new Path();
                crossRoad.moveTo(width * 0.18f, height * 0.05f);
                crossRoad.cubicTo(width * 0.34f, height * 0.34f,
                        width * 0.60f, height * 0.62f,
                        width * 0.90f, height * 0.92f);
                canvas.drawPath(crossRoad, road);

                Paint marker = new Paint(Paint.ANTI_ALIAS_FLAG);
                marker.setColor(Color.rgb(60, 160, 255));
                canvas.drawCircle(width * 0.50f, height * 0.53f,
                        Math.max(10f, width * 0.018f), marker);
                marker.setColor(Color.WHITE);
                marker.setStyle(Paint.Style.STROKE);
                marker.setStrokeWidth(4f);
                canvas.drawCircle(width * 0.50f, height * 0.53f,
                        Math.max(18f, width * 0.030f), marker);

                Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
                text.setColor(Color.WHITE);
                text.setTextSize(Math.max(22f, width * 0.028f));
                text.setFakeBoldText(true);
                canvas.drawText("HieuNav", width * 0.06f, height * 0.12f, text);
                text.setTextSize(Math.max(15f, width * 0.018f));
                text.setFakeBoldText(false);
                text.setColor(Color.rgb(210, 220, 228));
                canvas.drawText("Android Auto • Đang chờ hành trình",
                        width * 0.06f, height * 0.17f, text);
            } catch (Exception ignored) {
                // Surface can be replaced by the car host at any time.
            } finally {
                if (canvas != null) {
                    try {
                        surface.unlockCanvasAndPost(canvas);
                    } catch (Exception ignored) { }
                }
            }
        }
    }
}
