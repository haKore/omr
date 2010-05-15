package omr;

import java.awt.Rectangle;
import java.util.Observable;

/**
 * Represents a matrix of answer bubbles. 
 * @author Tapio Auvinen
 */
public class QuestionGroup extends Observable implements Comparable<QuestionGroup> {
    /**
     * Orientation of the question group. 
     * VERTICAL means that rows represent questions and comlumns represent alternatives.  
     * HORIZONTAL means that columns represent questions and rows represent alternatives.
     */
    public enum Orientation {
    	VERTICAL("vertical"), HORIZONTAL("horizontal"), STUDENT_NUMBER("student-number"), CHECK_LETTER("check-letter");
    	
    	private String name;
    	private Orientation(String name) {
    		this.name = name;
    	}
    	
    	@Override
        public String toString() {
    		return this.name;
    	}
    };
    
    public enum QuestionGroupEvent {
    	POSITION_CHANGED,   // Coordinates, dimensions or orientation is changed. Bubble values must be updated.
    	STRUCTURE_CHANGED,   // Row count, column count or index offset is changed. Sheet structure must be updated.
    	ANSWER_KEY_CHANGED
    }
    
    private int rowCount;      // number of question
    private int columnCount;   // number of alternatives
    private int indexOffset;         // number of the first question
    
    private Orientation orientation;
    private int leftX;           // x coordinate left-most bubble column's middlepoint
    private int rightX;          // x coordinate right-most bubble column's middlepoint
    private int topY;            // y coordinate of the top bubble row's middlepoint
    private int bottomY;         // y coordinate of the bottom bubble row's middlepoint
    
    private int bubbleWidth;
    private int bubbleHeight;
    
    private boolean[][] answerKey;  // [question][alternative]
    
    public QuestionGroup() {
        this(0,0,1,1);
    }
    
    public QuestionGroup(int leftX, int topY, int rightX, int bottomY) {
        this.leftX = leftX;
        this.topY = topY;
        this.rightX = rightX;
        this.bottomY = bottomY;
        this.bubbleWidth = 10;
        this.bubbleHeight = 10;
        this.orientation = Orientation.VERTICAL;
        
        this.rowCount = 10;
        this.columnCount = 4;
        
        initializeAnswerKey();
    }
    
    private void initializeAnswerKey() {
        if (orientation == Orientation.HORIZONTAL) {
        	this.answerKey = new boolean[columnCount][rowCount];
        } else {
        	this.answerKey = new boolean[rowCount][columnCount];
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.ANSWER_KEY_CHANGED);
    }
    
    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
    	if (this.orientation == orientation) {
    	    return;
    	}
        
        this.orientation = orientation;
    	
    	if (this.orientation == Orientation.CHECK_LETTER) {
    		this.setRowCount(2);
    		this.setColumnCount(10);
    	}

    	initializeAnswerKey();
    	
    	// Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.STRUCTURE_CHANGED);
    }
    
    public void setOrientation(String orientation) {
    	for (Orientation o : Orientation.values()) {
    		if (orientation.equals(o.toString())) {
    			this.setOrientation(o);
    			return;
    		}
    	}
    }

    /**
     * Returns the number of the first question. 
     */
    public int getIndexOffset() {
        return indexOffset;
    }

    /**
     * Sets the number of the first question.
     */
    public void setIndexOffset(int indexOffset) {
        this.indexOffset = indexOffset;
        
        // Notify observers 
        setChanged();
        notifyObservers(QuestionGroupEvent.STRUCTURE_CHANGED);
    }
    
    /**
     * Returns the x coordinate of the first column's middlepoint.  
     */
    public int getLeftX() {
        return leftX;
    }

    /**
     * Sets the x coordinate of the first column's middlepoint.  
     */
    public void setLeftX(int leftX) {
        this.leftX = leftX;
        
        if (this.leftX < 0) {
            this.leftX = 0;
        }
        
        if (this.leftX > this.rightX) {
            this.leftX = this.rightX;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
        
        if (this.rightX < this.leftX) {
            this.rightX = this.leftX;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
        
        if (this.topY < 0) {
            this.topY = 0;
        }
        
        if (this.topY > this.bottomY) {
            this.topY = this.bottomY;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
        
        if (this.bottomY < this.topY) {
            this.bottomY = this.topY;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }
    
    /**
     * Sets the location and size of the question group.
     * @param rect Rectangle that represents the middlepoints of the edge bubbles.
     */
    public void setBounds(Rectangle rect) {
        this.leftX = (int)rect.getX();
        this.topY = (int)rect.getY();
        this.rightX = (int)(rect.getX() + rect.getWidth());
        this.bottomY = (int)(rect.getY() + rect.getHeight());
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }
    
    /**
     * Returns the distance between first and last column's middlepoints in pixels.
     */
    public int getWidth() {
        return rightX - leftX;
    }

    /**
     * Returns the distance between first and last rows's middlepoints in pixels.
     */
    public int getHeight() {
        return bottomY - topY;
    }

    
    public int getBubbleWidth() {
        return bubbleWidth;
    }

    public void setBubbleWidth(int width) {
        this.bubbleWidth = width;
        
        if (this.bubbleWidth < 2) {
            this.bubbleWidth = 2;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }

    public int getBubbleHeight() {
        return bubbleHeight;
    }

    public void setBubbleHeight(int height) {
        this.bubbleHeight = height;
        
        if (this.bubbleHeight < 2) {
            this.bubbleHeight = 2;
        }
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.POSITION_CHANGED);
    }
    
    
    public void setRowCount(int rowCount) {
        if (this.rowCount == rowCount) {
            return;
        }
        
        this.rowCount = rowCount;
        initializeAnswerKey();
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.STRUCTURE_CHANGED);
    }
    
    public void setColumnCount(int columnCount) {
        if (this.columnCount == columnCount) {
            return;
        }
        
        this.columnCount = columnCount;
        initializeAnswerKey();
        
        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.STRUCTURE_CHANGED);
    }
    
    /**
     * Returns the number of rows in the question group.
     * If orientation is vertical, this is the same as questions count.
     * If orientation is horizontal, this is the same as alternatives count.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Returns the number of columns in the question group.
     * If orientation is vertical, this is the same as altenatives count.
     * If orientation is horizontal, this is the same as questions count.
     */
    public int getColumnCount() {
        return columnCount;
    }
    
    
    /**
     * Returns the number of questions in the question group.
     * If orientation is vertical, this is the same as row count.
     * If orientation is horizontal, this is the same as column count.
     */
    public int getQuestionsCount() {
        if (orientation == Orientation.HORIZONTAL) {
        	return getColumnCount();
        } else {
        	return getRowCount();
        }
    }

    /**
     * Returns the number of alternatives in the question group.
     * If orientation is vertical, this is the same as column count.
     * If orientation is horizontal, this is the same as row count.
     */
    public int getAlternativesCount() {
        if (orientation == Orientation.HORIZONTAL) {
        	return getRowCount();
        } else {
            return getColumnCount();
        }
    }
    
    public String getColumnLabel(int column) {
        if (orientation == Orientation.HORIZONTAL) {
        	return Integer.toString(getQuestionNumber(column));
        } else if (orientation == Orientation.CHECK_LETTER) {
        	return "";
        } else {
            return getAlternative(column);
        }
    }
    
    public String getRowLabel(int row) {
        if (orientation == Orientation.HORIZONTAL) {
        	return getAlternative(row);
        } else if (orientation == Orientation.CHECK_LETTER) {
        	return "";
        } else if (orientation == Orientation.STUDENT_NUMBER) {
        	return row + 1 + ".";
        } else {
            return Integer.toString(getQuestionNumber(row));
        }
    }
    
    public String getAlternative(int alternative) {
    	if (this.orientation == Orientation.STUDENT_NUMBER) {
    		return Integer.toString(alternative);
    	}
    	
    	return Character.toString((char)('A' + alternative));
    }
    
    /**
     * Returns the global question number, i.e. local question number + index offset.
     * @param question Local question number (row number)
     */
    public int getQuestionNumber(int question) {
        return this.indexOffset + question;
    }

    /**
     * Sets the correct answer, i.e. is the correct answer to select the given bubble.
     * @param question Local question number
     * @param alternative Alternative number
     * @param selected true if the correct answer is to select the given bubble.
     */
    public void setCorrectAnswer(int question, int alternative, boolean selected) {
        this.answerKey[question][alternative] = selected;

        // Notify observers
        setChanged();
        notifyObservers(QuestionGroupEvent.ANSWER_KEY_CHANGED);
    }
    
    /**
     * Returns the answer key as an array.
     * @return array of booleans [question][alternative], where true means that the correct answer is to select the bubble
     */
    public boolean[][] getAnswerKey() {
    	return this.answerKey;
    }
    
    /**
     * Tells whether the bubble should be selected
     * @param question Local question number [0, n]
     * @param alternative Alternative number
     * @return true if the corect answer is to select this bubble
     */
    public boolean getCorrectAnswer(int question, int alternative) {
        return this.answerKey[question][alternative];
    }
    
    /**
     * Returns the correct choices as a string, e.g. "AC"
     * @param question Local question number (row) [0,n]
     */
    public String getCorrectChoices(int question) {
        if (this.answerKey == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int alternative = 0; alternative < this.getAlternativesCount(); alternative++) {
            if (answerKey[question][alternative]) {
                result.append(getAlternative(alternative));
            }
        }
        
        return result.toString();
    }

    public int compareTo(QuestionGroup other) {
        return this.indexOffset - other.indexOffset;
    }
    
}
