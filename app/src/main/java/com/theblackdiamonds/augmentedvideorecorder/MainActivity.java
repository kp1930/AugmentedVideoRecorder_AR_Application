package com.theblackdiamonds.augmentedvideorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private VideoRecorder videoRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        assert arFragment != null;
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) ->
                MaterialFactory.makeOpaqueWithColor(MainActivity.this, new Color(android.graphics.Color.RED))
                        .thenAccept(material -> {
                            ModelRenderable modelRenderable = ShapeFactory.makeSphere(0.3f,
                                    new Vector3(0f, 0.3f, 0f), material);

                            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
                            anchorNode.setRenderable(modelRenderable);
                            arFragment.getArSceneView().getScene().addChild(anchorNode);
                        })
        ));

        Button button = findViewById(R.id.button_record);
        button.setOnClickListener(view -> {
            if (videoRecorder == null) {
                videoRecorder = new VideoRecorder();
                videoRecorder.setSceneView(arFragment.getArSceneView());

                int orientation = getResources().getConfiguration().orientation;
                videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation);
            }

            boolean isRecording = videoRecorder.onToggleRecord();

            if (isRecording)
                Toast.makeText(MainActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "End Recording", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}