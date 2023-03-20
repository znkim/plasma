import org.lwjgl.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    // The window handle
    private long window;
    private Camera camera;
    private int shaderProgram;
    private ArrayList<RenderableObject> renderableObjects;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        renderableObjects = new ArrayList<RenderableObject>();

        init();
        loop();

        // 창의 콜백을 해제하고 창을 삭제합니다.
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // GLFW를 종료하고 에러 콜백을 해제합니다.
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // 에러 콜백을 설정합니다. System.err의 에러 메세지를 출력 기본으로 구현합니다.
        GLFWErrorCallback.createPrint(System.err).set();

        // GLFW를 초기화 합니다. 이 함수를 실행하기 전까지 다른 기능은 실행되지 않습니다.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // GLFW 설정값
        glfwDefaultWindowHints(); // 선택 기능으로 창의 힌트는 기본 값으로 설정되어있습니다.
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // 창을 만든 후에 창을 숨긴상태로 합니다.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // 창의 사이즈를 조절할 수 있게 합니다.
        //glfwGetCurrentContext();

        // 창을 생성합니다.
        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // 키보드 콜백 이벤트를 설정합니다. 키를 눌렀을 때, 누르고 있을 때, 떼었을 때에 따라 바꿔줍니다.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // 스레드 스텍을 가져와서 새 프레임에 추가합니다.
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // glfwCreateWindow 에서 창 사이즈를 가져옵니다.
            glfwGetWindowSize(window, pWidth, pHeight);

            // 모니터의 해상도를 가져옵니다.
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // 모니터 중앙에 창을 위치시킵니다.
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // 스택 프레임이 자동으로 팝업됩니다.
        // Make the OpenGL context current
        // OpenGL 컨텍스트를 최신으로 적용합니다.
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // 수직동기화를 활성화 합니다.
        glfwSwapInterval(1);

        // 창을 표시합니다.
        glfwShowWindow(window);

        setupShader();
        this.camera = new Camera();
    }

    private void setupShader() {
        GL.createCapabilities();
        String vertexShaderSource = getShaderSource("default.vs.glsl");
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);

        String fragmentShaderSource = getShaderSource("default.fs.glsl");
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);

        GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(GL20.glGetShaderInfoLog(vertexShader, GL20.glGetShaderi(vertexShader, GL20.GL_INFO_LOG_LENGTH)));
            System.err.println(GL20.glGetShaderInfoLog(fragmentShader, GL20.glGetShaderi(fragmentShader, GL20.GL_INFO_LOG_LENGTH)));

            System.err.println("Program failed to link");
            System.err.println(GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH)));
        }


        //.gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)

        GL20.glUseProgram(program);

        this.shaderProgram = program;
    }

    private String getShaderSource(String path) {
        URL resource = getClass().getClassLoader().getResource("./shader/" +path);
        String filePath = resource.getFile();

        StringBuffer buffer = new StringBuffer();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.ready()){
                buffer.append(reader.readLine());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        String shaderSource = buffer.toString();
        //System.out.println(shaderSource);
        //System.out.println(filePath);
        return shaderSource;
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        //GL.createCapabilities();



        //GL20.glUseProgram();

        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(window, width, height);
        float fovy = 1.5708f;
        float aspect = width[0] / height[0];
        float near = 0.1f;
        float far = 1000f;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(fovy, aspect, near, far);

        Matrix4f modelViewMatrix = this.camera.getTransformMatrix();

        // 클리어 컬러를 적용합니다.
        glClearColor(0.8f, 0.8f, 0.8f, 0.8f);

        int aVertexPosition = GL20.glGetAttribLocation(this.shaderProgram, "aVertexPosition");
        int aVertexColor = GL20.glGetAttribLocation(this.shaderProgram, "aVertexColor");

        int uProjectionMatrix = GL20.glGetUniformLocation(this.shaderProgram, "uProjectionMatrix");
        int uModelRotationMatrix = GL20.glGetUniformLocation(this.shaderProgram, "uModelRotationMatrix");
        int uObjectRotationMatrix = GL20.glGetUniformLocation(this.shaderProgram, "uObjectRotationMatrix");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer projectionMatrixBuffer = stack.mallocFloat(16);
            projectionMatrix.get(projectionMatrixBuffer);
            FloatBuffer modelViewMatrixBuffer = stack.mallocFloat(16);
            modelViewMatrix.get(modelViewMatrixBuffer);

            GL20.glUniformMatrix4fv(uProjectionMatrix, false, projectionMatrixBuffer);
            GL20.glUniformMatrix4fv(uModelRotationMatrix, false, modelViewMatrixBuffer);
            //GL20.glUniformMatrix4fv(uModelRotationMatrix, projectionMatrix);

            renderableObjects.forEach(renderableObject -> {
                renderableObject.render(this.shaderProgram);
            });
        }
        //System.out.println(this.shaderProgram);
        //System.out.println(aVertexPosition);
        //System.out.println(aVertexColor);
        //System.out.println(uProjectionMatrix);
        //System.out.println(uModelRotationMatrix);
        //System.out.println(uObjectRotationMatrix);

        // 사용자가 창을 닫거다 esc키를 누를 때까지 랜더링 루프를 실행합니다.
        while (!glfwWindowShouldClose(window)) {
            // 프레임 버퍼 클리어
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            // 색상버퍼 교체
            glfwSwapBuffers(window);
            // 이벤트를 폴링상태로 둡니다. key 콜백이 실행되려면 폴링상태가 활성화 되어있어야 합니다.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }
}