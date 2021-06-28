<template>
  <div>
<!--    <q-btn flat round class="btn-fullscreen" icon="fullscreen" color="primary"/> NOT YET IMPLEMENTED-->
    <div>
      <q-skeleton v-if="loading" height="600px" square/>
    </div>
    <div>
      <q-card bordered flat>
        <div class="scene" ref="scene"/>
      </q-card>
    </div>
  </div>
</template>

<script lang="js">
import {WEBGL} from "./WebGL"
import * as Three from "three";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls"
import {Text} from "troika-three-text"

export default {
  name: 'ai-visualisation',
  data() {
    return {
      cameraPerspective: null,
      renderer: new Three.WebGLRenderer({alpha: true, antialias: true}),
      scene: new Three.Scene(),
      material: new Three.MeshBasicMaterial({color: 0x007EA7}),
      disposables: [],
      nonDisposables: [],
      loading: true,
      transition: false,
      height: 600,
    }
  },
  props: {
    id: null,
    neuralNet: null,
    labels: null,
    inputShape: null,
  },
  watch: {
    id: function () {
      this.$q.loading.hide()
      this.clearScene();
    }
  },
  methods: {
    fillScene() {
      // Take the list, but break the link/reference
      let layers = this.neuralNet.layers.slice(0);
      layers.unshift({inputShape: this.inputShape, name: "Input layer"});

      // Const values that are mostly fingerspitzengefühl
      const xDistanceFactor = 2;
      const xDistancePerLayer = 10 * layers.length;
      const zViewDistanceModifier = 4;

      // Determine the whole width of the visualisation
      let width = 0;
      for (const layer of layers) {
        let sizeX = 1;
        if (layer.inputShape[2] !== undefined) {
          sizeX = layer.inputShape[2];
        }
        width += (sizeX * xDistanceFactor) + xDistancePerLayer
      }

      this.cameraPerspective.position.setZ(width/zViewDistanceModifier);
      const fontSize = width / (layers.length * 3);

      // Determine the starting point when going left to right
      let start = -(width / 2);

      // Visualise all layers
      for (let i = 0; i < layers.length; i++) {
        // Determine the width of the current layer
        let sizeX = 1;
        if (layers[i].inputShape[2] !== undefined) {
          sizeX = layers[i].inputShape[2];
        }

        // Allocate enough room for the layer and text, move over to the starting position for the next layer
        start += (sizeX * xDistanceFactor) + xDistancePerLayer;

        // Visualise the layer
        this.addLayer(this.scene, start - sizeX, layers[i], fontSize);


      }

      // Stop loading and disable the skeleton overlay
      this.loading = false;

    },
    addLayer(scene, xOffset, layer, fontSize) {

      // Determine all sizes in the inputshape
      let sizeX, sizeY, amount;

      if (layer.inputShape[3] !== undefined) {
        amount = layer.inputShape[3];
      } else {
        amount = 1;
      }

      if (layer.inputShape[2] !== undefined) {
        sizeX = layer.inputShape[2];
      } else {
        sizeX = 1;
      }

      if (layer.inputShape[1] !== undefined) {
        sizeY = layer.inputShape[1];
      } else {
        sizeY = 1;
      }

      // Determine the vertical offset based on the amount of x*y nodes
      const amountOffset = -(amount / 2);

      //Add elements to scene
      for (let i = 0; i < amount; i++) {

        // Determine the vertical offset for the current 'node'
        // const yOffset = -(sizeY / 2) + ((amountOffset + i) * (sizeX * 1.5)) + 2;
        const yOffset = ((amountOffset + i) * (sizeY));
        const yPos = yOffset + sizeY / 2 + ((i + 0.5 - amount/2)*0.5*sizeY);

        // Create a plane
        const geometry = new Three.PlaneGeometry(sizeX, sizeY);
        this.disposables.push(geometry);

        // Add node to scene
        const node = new Three.Mesh(geometry, this.material);
        node.position.x = xOffset;
        node.position.y = yPos;
        this.scene.add(node);
        this.nonDisposables.push(node);

        // Add grid to scene
        if (sizeX === sizeY) {
          const grid = new Three.GridHelper(sizeX, sizeY, '#00A8E8', '#00A8E8');
          grid.rotation.x = 1.57079632679;
          grid.position.z = 0.01;
          grid.position.x = xOffset;
          grid.position.y = yPos;

          this.scene.add(grid);
          this.nonDisposables.push(grid);
        }
      }

      // Add text to the scene for every layer
      const layerName = new Text();
      layerName.text = `${layer.name}   (${sizeX}x${sizeY}) x (${amount})`;
      layerName.anchorX = 'center';
      layerName.anchorY = 'center';
      layerName.fontSize = fontSize;
      layerName.color = 0x00A8E8;
      layerName.position.x = xOffset - sizeX / 2 - 3;
      layerName.position.y = 0;
      layerName.rotation.z = -1.57079632679;
      this.scene.add(layerName);

      // Update the rendering:
      layerName.sync();

      // Add text to a list of disposables, to remove later
      this.disposables.push(layerName);

    },
    clearScene() {
      // Remove all elements from the scene that can not be disposed with dispose()
      this.nonDisposables.forEach(el => {
        this.scene.remove(el);
      })
      this.nonDisposables.splice(0, this.nonDisposables.length);

      // Clear all disposables
      this.disposables.forEach(el => {
        this.scene.remove(el);
        el.dispose();
      });
      // Clear up memory consumed by array
      this.disposables.splice(0, this.disposables.length);
    },
    mountScene() {
      console.log("Mounting new scene");
      const el = this.$refs.scene;

      // Set-up camera
      this.cameraPerspective = new Three.PerspectiveCamera(
          110,
          1,
          0.1,
          2000
      );
      this.cameraPerspective.position.z = 500;

      // Set all controls
      const controls = new OrbitControls(this.cameraPerspective, this.renderer.domElement);
      controls.enableZoom = true;
      controls.enableRotate = false;
      controls.enableDamping = true;
      controls.dampingFactor = 0.25;
      controls.minDistance = 1;
      controls.maxDistance = 2000;
      controls.mouseButtons = {
        LEFT: Three.MOUSE.PAN,
        MIDDLE: Three.MOUSE.ROTATE,
        RIGHT: Three.MOUSE.DOLLY
      }

      // Set the canvas size of the renderer
      this.renderer.setSize(el.clientWidth, this.height);
      el.appendChild(this.renderer.domElement);

      // Visualise the neural net
      this.fillScene();

      // Set listener to fix scene upon resize
      const onWindowResize = () => {
        this.renderer.setSize(el.clientWidth, this.height);
        this.cameraPerspective.aspect = el.clientWidth / this.height;
        this.cameraPerspective.updateProjectionMatrix();
      }

      // Update canvas
      const animate = () => {
        requestAnimationFrame(animate);
        this.renderer.render(this.scene, this.cameraPerspective);
      };

      // Error handling for incompatible browsers
      if (WEBGL.isWebGLAvailable()) {
        // Initiate function or other initializations here
        onWindowResize();
        window.addEventListener('resize', onWindowResize);
        animate();
      } else {
        const warning = WEBGL.getWebGLErrorMessage();
        document.getElementById('container').appendChild(warning);
      }
    }
  },
  mounted() {
    this.transition = true;

    // Start mountScene after creating skeleton
    setTimeout(() => {
      this.mountScene();
    }, 50);
  },
  beforeDestroy() {
    this.clearScene()
    this.renderer.getContext("webgl").getExtension('WEBGL_lose_context').loseContext();
  }
}
</script>

<style scoped>
.btn-fullscreen{
  z-index: 1;
  position: relative;
  top: 40px;
  left: 0;

}

</style>