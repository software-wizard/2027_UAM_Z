package pl.uam.chess.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetGame() throws Exception {
        mockMvc.perform(post("/api/game/reset")).andExpect(status().isOk());
    }

    @Test
    void clickSelectsThenMovesAndAnIllegalSquareClearsTheSelection() throws Exception {
        mockMvc.perform(get("/api/game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value("WHITE"))
                .andExpect(jsonPath("$.selected").value(nullValue()))
                .andExpect(jsonPath("$.board[6][4].type").value("PAWN"))
                .andExpect(jsonPath("$.legalMoves", hasSize(0)));

        mockMvc.perform(post("/api/game/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"row\":6,\"col\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected.row").value(6))
                .andExpect(jsonPath("$.selected.col").value(4))
                .andExpect(jsonPath("$.legalMoves", hasSize(2)))
                .andExpect(jsonPath("$.legalMoves[0].capture").value(false));

        mockMvc.perform(post("/api/game/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"row\":0,\"col\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected").value(nullValue()))
                .andExpect(jsonPath("$.turn").value("WHITE"))
                .andExpect(jsonPath("$.board[6][4].type").value("PAWN"));

        mockMvc.perform(post("/api/game/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"row\":6,\"col\":4}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/game/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"row\":4,\"col\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value("BLACK"))
                .andExpect(jsonPath("$.selected").value(nullValue()))
                .andExpect(jsonPath("$.board[4][4].type").value("PAWN"))
                .andExpect(jsonPath("$.board[4][4].color").value("WHITE"))
                .andExpect(jsonPath("$.board[6][4]").value(nullValue()));
    }
}
