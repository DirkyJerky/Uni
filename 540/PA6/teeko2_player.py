import random
import copy
import statistics

class Teeko2Player:
    """ An object representation for an AI game player for the game Teeko2.
    """
    board = [[' ' for j in range(5)] for i in range(5)]
    pieces = ['b', 'r']

    def __init__(self):
        """ Initializes a Teeko2Player object by randomly selecting red or black as its
        piece color.
        """
        self.my_piece = random.choice(self.pieces)
        self.opp = self.pieces[0] if self.my_piece == self.pieces[1] else self.pieces[1]

    def make_move(self, state):
        """ Selects a (row, col) space for the next move. You may assume that whenever
        this function is called, it is this player's turn to move.

        Args:
            state (list of lists): should be the current state of the game as saved in
                this Teeko2Player object. Note that this is NOT assumed to be a copy of
                the game state and should NOT be modified within this method (use
                place_piece() instead). Any modifications (e.g. to generate successors)
                should be done on a deep copy of the state.

                In the "drop phase", the state will contain less than 8 elements which
                are not ' ' (a single space character).

        Return:
            move (list): a list of move tuples such that its format is
                    [(row, col), (source_row, source_col)]
                where the (row, col) tuple is the location to place a piece and the
                optional (source_row, source_col) tuple contains the location of the
                piece the AI plans to relocate (for moves after the drop phase). In
                the drop phase, this list should contain ONLY THE FIRST tuple.

        Note that without drop phase behavior, the AI will just keep placing new markers
            and will eventually take over the board. This is not a valid strategy and
            will earn you no points.
        """

        # drop_phase = True   # TODO: detect drop phase

        # if not drop_phase:
        #     # TODO: choose a piece to move and remove it from the board
        #     # (You may move this condition anywhere, just be sure to handle it)
        #     #
        #     # Until this part is implemented and the move list is updated
        #     # accordingly, the AI will not follow the rules after the drop phase!
        #     pass

        # # select an unoccupied space randomly
        # # TODO: implement a minimax algorithm to play better
        # move = []
        # (row, col) = (random.randint(0,4), random.randint(0,4))
        # while not state[row][col] == ' ':
        #     (row, col) = (random.randint(0,4), random.randint(0,4))

        # # ensure the destination (row,col) tuple is at the beginning of the move list
        # move.insert(0, (row, col))
        # return move

        def minimax(move, state, depth, isAITurn):
            """
            arg move = the `move` that led to `state`

            Returns (move, h)
            """
            h = self.heuristic(state)

            if depth == 0 or abs(h) == 1:
                return (move, h)

            if isAITurn:
                bestVal = -2
                bestMove = None
                for (succMove, succ) in self.successors(state, True):
                    _, mmxVal = minimax(succMove, succ, depth - 1, False)
                    if mmxVal > bestVal:
                        bestVal = mmxVal
                        bestMove = succMove
                return (bestMove, bestVal)

            else:
                bestVal = 2
                bestMove = None
                for (succMove, succ) in self.successors(state, False):
                    _, mmxVal = minimax(succMove, succ, depth - 1, True)
                    if mmxVal < bestVal:
                        bestVal = mmxVal
                        bestMove = succMove
                return (bestMove, bestVal)

        
        bestMove, _ = minimax(None, state, 3, True)

        return bestMove



    def successors(self, state, isAITurn):
        """
        Returns a list of successors for a state

        Returns: [(move, newState)]
        """
        isDropPhase = 8 != sum([sum([1 if cell in self.pieces else 0 for cell in row]) for row in state])

        piece = self.my_piece if isAITurn else self.opp

        if isDropPhase:
            succs = []

            # Iterate over drop points
            for rowI in range(5):
                for colI in range(5):
                    if state[rowI][colI] == ' ':
                        newSucc = ([(rowI, colI)], copy.deepcopy(state))
                        newSucc[1][rowI][colI] = piece
                        succs.append(newSucc)
            
            return succs
        
        else:
            succs = []

            # Iterate over source points
            for rowI in range(5):
                for colI in range(5):
                    if state[rowI][colI] == piece:
                        # Iterate over destination points
                        for (rowOff, colOff) in [(1,1),(1,0),(1,-1),(0,1),(0,-1),(-1,1),(-1,0),(-1,-1)]:
                            if 0 <= rowI+rowOff < 5 and 0 <= colI+colOff < 5:
                                if state[rowI+rowOff][colI+colOff] == ' ':
                                    newSucc = ([(rowI+rowOff, colI+colOff), (rowI, colI)], copy.deepcopy(state))
                                    newSucc[1][rowI][colI] = ' '
                                    newSucc[1][rowI+rowOff][colI+colOff] = piece
                                    succs.append(newSucc)
            
            return succs

    def opponent_move(self, move):
        """ Validates the opponent's next move against the internal board representation.
        You don't need to touch this code.

        Args:
            move (list): a list of move tuples such that its format is
                    [(row, col), (source_row, source_col)]
                where the (row, col) tuple is the location to place a piece and the
                optional (source_row, source_col) tuple contains the location of the
                piece the AI plans to relocate (for moves after the drop phase). In
                the drop phase, this list should contain ONLY THE FIRST tuple.
        """
        # validate input
        if len(move) > 1:
            source_row = move[1][0]
            source_col = move[1][1]
            if source_row != None and self.board[source_row][source_col] != self.opp:
                raise Exception("You don't have a piece there!")
        if self.board[move[0][0]][move[0][1]] != ' ':
            raise Exception("Illegal move detected")
        # make move
        self.place_piece(move, self.opp)

    def place_piece(self, move, piece):
        """ Modifies the board representation using the specified move and piece

        Args:
            move (list): a list of move tuples such that its format is
                    [(row, col), (source_row, source_col)]
                where the (row, col) tuple is the location to place a piece and the
                optional (source_row, source_col) tuple contains the location of the
                piece the AI plans to relocate (for moves after the drop phase). In
                the drop phase, this list should contain ONLY THE FIRST tuple.

                This argument is assumed to have been validated before this method
                is called.
            piece (str): the piece ('b' or 'r') to place on the board
        """
        if len(move) > 1:
            self.board[move[1][0]][move[1][1]] = ' '
        self.board[move[0][0]][move[0][1]] = piece

    def print_board(self):
        """ Formatted printing for the board """
        for row in range(len(self.board)):
            line = str(row)+": "
            for cell in self.board[row]:
                line += cell + " "
            print(line)
        print("   A B C D E")
        # print("heuristic values =" + str(self.heuristic_values(self.board)))
        # print("heuristic =" + str(self.heuristic(self.board)))

    def game_value(self, state):
        """ Checks the current board status for a win condition

        Args:
        state (list of lists): either the current state of the game as saved in
            this Teeko2Player object, or a generated successor state.

        Returns:
            int: 1 if this Teeko2Player wins, -1 if the opponent wins, 0 if no winner
        """

        h = self.heuristic(state)
        if abs(h) == 1:
            return h
        
        return 0

    def heuristic(self, state):
        """ A heuristic of the state argument that:
          = 1 if The AI won
          = -1 if the player won
          otherwise = the average of all "significant" heuristic values from the function below
            Significant in this case means |value| >= 0.50"""
        CUTOFF = 0.50

        h_values = self.heuristic_values(state)

        if max(h_values) == 1:
            return 1
        elif min(h_values) == -1:
            return -1

        sig_values = list(filter(lambda value: abs(value) >= CUTOFF, h_values))
        
        return statistics.mean(sig_values) if len(sig_values) != 0 else 0
        


    def heuristic_values(self, state):
        """ Iterates over all possible win configurations (each row/column/diagonal of four, and the diamonds),
        and returns a "score" for each.  The score, from -1 to 1, represents how close to a winning configuration
        each configuration is.  Closer to 1 is better for AI, closer to -1 is better for player.
        Any score of 1 or -1 means the respective player won.
        This returns a list, for further processing """

        scores = []

        # Row check
        for row in state:
            for i in range(2):
                score = 0.0
                for col in range(i, i+4):
                    if row[col] == self.my_piece:
                        score += 0.25
                    elif row[col] == self.opp:
                        score -= 0.25
                scores.append(score)

        # Column check
        for col in range(5):
            for i in range(2):
                score = 0.0
                for row in range(i, i+4):
                    if state[row][col] == self.my_piece:
                        score += 0.25
                    elif state[row][col] == self.opp:
                        score -= 0.25
                scores.append(score)

        # Diagonal checks
        for rowI in range(2):
            for colI in range(2):
                # UpperLeft-LowerRight
                score = 0.0
                for i in range(4):
                    if state[rowI+i][colI+i] == self.my_piece:
                        score += 0.25
                    elif state[rowI+i][colI+i] == self.opp:
                        score -= 0.25
                scores.append(score)

                # LowerLeft-UpperRight
                score = 0.0
                for i in range(4):
                    if state[4-rowI-i][colI+i] == self.my_piece:
                        score += 0.25
                    elif state[4-rowI-i][colI+i] == self.opp:
                        score -= 0.25
                scores.append(score)

        # Diamond checks
        for rowI in range(1,4):
            for colI in range(1,4):
                score = 0.0
                for (rowOff, colOff) in [(0,1),(1,0),(0,-1),(-1,0)]:
                    if state[rowI+rowOff][colI+colOff] == self.my_piece:
                        score += 0.25
                    elif state[rowI+rowOff][colI+colOff] == self.opp:
                        score -= 0.25

                # Penalize by half if the middle isnt clear
                if state[rowI][colI] != ' ':
                    score /= 2
                scores.append(score)

        return scores

############################################################################
#
# THE FOLLOWING CODE IS FOR SAMPLE GAMEPLAY ONLY
#
############################################################################

ai = Teeko2Player()
piece_count = 0
turn = 0

# drop phase
while piece_count < 8 and abs(ai.game_value(ai.board)) != 1:

    # get the player or AI's move
    if ai.my_piece == ai.pieces[turn]:
        ai.print_board()
        move = ai.make_move(ai.board)
        ai.place_piece(move, ai.my_piece)
        print(ai.my_piece+" moved at "+chr(move[0][1]+ord("A"))+str(move[0][0]))
    else:
        move_made = False
        ai.print_board()
        print(ai.opp+"'s turn")
        while not move_made:
            player_move = input("Move (e.g. B3): ")
            while player_move[0] not in "ABCDE" or player_move[1] not in "01234":
                player_move = input("Move (e.g. B3): ")
            try:
                ai.opponent_move([(int(player_move[1]), ord(player_move[0])-ord("A"))])
                move_made = True
            except Exception as e:
                print(e)

    # update the game variables
    piece_count += 1
    turn += 1
    turn %= 2

# move phase - can't have a winner until all 8 pieces are on the board
while ai.game_value(ai.board) == 0:

    # get the player or AI's move
    if ai.my_piece == ai.pieces[turn]:
        ai.print_board()
        move = ai.make_move(ai.board)
        ai.place_piece(move, ai.my_piece)
        print(ai.my_piece+" moved from "+chr(move[1][1]+ord("A"))+str(move[1][0]))
        print("  to "+chr(move[0][1]+ord("A"))+str(move[0][0]))
    else:
        move_made = False
        ai.print_board()
        print(ai.opp+"'s turn")
        while not move_made:
            move_from = input("Move from (e.g. B3): ")
            while move_from[0] not in "ABCDE" or move_from[1] not in "01234":
                move_from = input("Move from (e.g. B3): ")
            move_to = input("Move to (e.g. B3): ")
            while move_to[0] not in "ABCDE" or move_to[1] not in "01234":
                move_to = input("Move to (e.g. B3): ")
            try:
                ai.opponent_move([(int(move_to[1]), ord(move_to[0])-ord("A")),
                                 (int(move_from[1]), ord(move_from[0])-ord("A"))])
                move_made = True
            except Exception as e:
                print(e)

    # update the game variables
    turn += 1
    turn %= 2

ai.print_board()
if ai.game_value(ai.board) == 1:
    print("AI wins! Game over.")
else:
    print("You win! Game over.")
