package com.sam.webtasks.iotask2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.AnimationProperty.Properties;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sam.webtasks.basictools.Names;
import com.sam.webtasks.basictools.PHP;
import com.sam.webtasks.basictools.ProgressBar;
import com.sam.webtasks.client.Params;
import com.sam.webtasks.client.SequenceHandler;
import com.sam.webtasks.iotask1.IOtask1BlockContext;
import com.sam.webtasks.iotask1.IOtask1DisplayParams;

public class IOtask2RunTrial {

	public static void Run() {
		int m = IOtask2BlockContext.countdownTime() / 60;
		int s = IOtask2BlockContext.countdownTime() % 60;
		String tLabel;

		if (s < 10) {
			tLabel = " " + m + ":0" + s;
		} else {
			tLabel = " " + m + ":" + s;
		}

		final Label timerLabel = new Label(tLabel);

		final Timer trialTimer = new Timer() {
			public void run() {
				int minutes = IOtask2BlockContext.countdownTime() / 60;
				int seconds = IOtask2BlockContext.countdownTime() % 60;

				if (seconds < 10) {
					timerLabel.setText(" " + minutes + ":" + "0" + seconds);
				} else {
					timerLabel.setText(" " + minutes + ":" + seconds);
				}

				IOtask2BlockContext.countdown();

				if (IOtask2BlockContext.countdownTime() < 1) {
					timerLabel.addStyleName("red");
					timerLabel.setText("Out of time. Please go faster.");
					cancel();
				}
			}
		};

		ProgressBar.SetProgress(Params.progress++, Params.nTrials);
		
		PHP.logData("payment",  IOtask2BlockContext.getMoneyString(), false);

		// get block context
		IOtask2Block block = IOtask2BlockContext.getContext();

		// set up labels to put inside circles
		final String[] labels = new String[block.nCircles];

		for (int i = 0; i < block.totalCircles; i++) {
			if (block.ongoingStimType == Names.ONGOING_STIM_NUMBERS) {
				labels[i] = "" + (i + 1);
			}

			if (block.ongoingStimType == Names.ONGOING_STIM_LETTERS) {
				labels[i] = String.valueOf((char) (65 + i));
			}

			if (block.ongoingStimType == Names.ONGOING_STIM_NUMBERS_DESCENDING) {
				labels[i] = "" + (block.totalCircles - i);
			}
		}

		// set size parameters
		int xDim = Window.getClientWidth();
		int yDim = Window.getClientHeight();
		int minDim = 0; // smaller of the two dimensions

		if (xDim <= yDim) {
			minDim = xDim;
		} else {
			minDim = yDim;
		}

		final int boxSize = (int) (minDim * IOtask2DisplayParams.boxSize);
		int margin = (int) (boxSize * IOtask2DisplayParams.margin);
		final int circleRadius = (int) (boxSize * IOtask2DisplayParams.circleRadius);

		final LienzoPanel panel = new LienzoPanel(boxSize, boxSize);

		// Window.setMargin("0px");
		final VerticalPanel verticalPanel = new VerticalPanel();

		verticalPanel.setWidth(Window.getClientWidth() + "px");
		verticalPanel.setHeight(Window.getClientHeight() + "px");

		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		final VerticalPanel lienzoWrapper = new VerticalPanel();

		// timer display

		final VerticalPanel timerWrapper = new VerticalPanel();
		timerWrapper.setWidth(boxSize + "px");
		timerWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		timerWrapper.add(timerLabel);
		timerLabel.setStyleName("livePointsDisplay");

		if (IOtask2BlockContext.countdownTimer()) {
			lienzoWrapper.add(timerWrapper);
			trialTimer.cancel();
			IOtask2BlockContext.setCountdownTime(Params.countdownTime);
			trialTimer.scheduleRepeating(1000);
		}

		// points display

		final VerticalPanel pointsWrapper = new VerticalPanel();
		pointsWrapper.setWidth(boxSize + "px");
		pointsWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		final HTML pointsDisplay = new HTML("You have scored " + IOtask2BlockContext.getTotalPoints() + " points ("
				+ IOtask2BlockContext.getMoneyString() + ")");

		pointsDisplay.setStyleName("livePointsDisplay");
		pointsWrapper.add(pointsDisplay);

		if (block.showLivePoints) {
			lienzoWrapper.add(pointsWrapper);
		}

		lienzoWrapper.add(panel);

		final HorizontalPanel lienzoWrapperWrapper = new HorizontalPanel();

		lienzoWrapperWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		final HTML leftPoints = new HTML("");
		final HTML rightPoints = new HTML("");

		if (block.showPointLabels) {
			int[] pointValues = IOtask2BlockContext.getPointValues();
			
			leftPoints.setHTML("+" + pointValues[1]);
			rightPoints.setHTML("+" + pointValues[2]);
			leftPoints.addStyleName("pointHTML");
			rightPoints.addStyleName("pointHTML");
		}

		lienzoWrapperWrapper.add(leftPoints);
		lienzoWrapperWrapper.add(lienzoWrapper);
		lienzoWrapperWrapper.add(rightPoints);

		verticalPanel.add(lienzoWrapperWrapper);

		RootPanel.get().add(verticalPanel);

		// set up outline
		Layer bgLayer = new Layer();

		Line left = new Line(0, 0, 0, boxSize).setStrokeColor(IOtask2DisplayParams.circleColours[1]).setStrokeWidth(10);
		Line right = new Line(boxSize, 0, boxSize, boxSize).setStrokeColor(IOtask2DisplayParams.circleColours[2])
				.setStrokeWidth(10);
		Line bottom = new Line(0, boxSize, boxSize, boxSize).setStrokeColor(ColorName.BLACK).setStrokeWidth(10);
		Line top = new Line(0, 0, boxSize, 0).setStrokeColor(IOtask2DisplayParams.circleColours[3]).setStrokeWidth(10);

		bgLayer.add(left);
		bgLayer.add(right);
		bgLayer.add(bottom);
		bgLayer.add(top);

		// put background layer on screen
		panel.add(bgLayer);

		// set up circles
		final Layer circleLayer = new Layer();

		int[] circleX = new int[block.nCircles];
		int[] circleY = new int[block.nCircles];
		final Circle[] circles = new Circle[block.nCircles];
		final Circle[] circleOverlays = new Circle[block.nCircles];
		final Text[] circleText = new Text[block.nCircles];
		final Group[] circleGroup = new Group[block.nCircles];
		int randomFlag = 0;
		IOtask2BlockContext.setCompletedCircles(0);
		IOtask2BlockContext.setNextCircle(0);
		IOtask2BlockContext.setCircleAdjust(0);
		IOtask2BlockContext.setnHits(0);

		int c = 0;
		int randomCounter = 0;

		while (c < block.nCircles) {
			randomFlag = 1;

			while (randomFlag == 1) {
				randomCounter++;

				if (randomCounter > 10000) {
					randomCounter = 0;
					c = 0; // start again if failing to position the circles appropriately
				}

				randomFlag = 0;

				circleX[c] = Random.nextInt(boxSize - (2 * circleRadius) - (2 * margin)) + circleRadius + margin;
				circleY[c] = Random.nextInt(boxSize - (2 * circleRadius) - (2 * margin)) + circleRadius + margin;

				for (int cc = 0; cc < c; cc++) {
					int distanceX = circleX[c] - circleX[cc];
					int distanceY = circleY[c] - circleY[cc];

					double distanceAbs = Math.pow(Math.pow(distanceX, 2) + Math.pow(distanceY, 2), 0.5);

					if (distanceAbs < (3 * circleRadius)) {
						randomFlag = 1;
					}
				}
			}

			c++;
		}

		final Date trialStart = new Date();

		final int[] circleXref = circleX;
		final int[] circleYref = circleY;

		for (c = 0; c < block.nCircles; c++) {
			circles[c] = new Circle(circleRadius);
			circleOverlays[c] = new Circle(circleRadius);
			circleText[c] = new Text(labels[c], "Verdana, sans-serif", null, IOtask2DisplayParams.circleTextSize);
			circleGroup[c] = new Group();

			circles[c].setFillColor(IOtask2DisplayParams.circleColours[0]);
			circles[c].setStrokeColor(ColorName.BLACK);
			circles[c].setStrokeWidth(2);

			circleText[c].setTextAlign(TextAlign.CENTER);
			circleText[c].setTextBaseLine(TextBaseLine.MIDDLE);
			circleText[c].setFillColor(ColorName.BLACK);

			circleOverlays[c].setFillColor(ColorName.BLACK);
			circleOverlays[c].setAlpha(0.0000001);

			circleGroup[c].add(circles[c]);
			circleGroup[c].add(circleText[c]);
			circleGroup[c].add(circleOverlays[c]);
			circleGroup[c].setX(circleX[c]);
			circleGroup[c].setY(circleY[c]);

			switch (IOtask2BlockContext.getOffloadCondition()) {
			case Names.REMINDERS_NOTALLOWED:
				circleGroup[c].setDraggable(false);
				break;
			default:
				circleGroup[c].setDraggable(true);
				break;
			}

			circleGroup[c].setDragBounds(new DragBounds(1, 1, boxSize, boxSize));

			circleLayer.add(circleGroup[c]);

			final int finalc = c; // need to set up a final version of the c variable so that it works in the code
									// below

			circleGroup[c].addNodeDragStartHandler(new NodeDragStartHandler() {		
				@Override
				
				public void onNodeDragStart(NodeDragStartEvent event) {		
					// reset the double click flag. this is used to quit the task
					// using a double click to the first circle followed by
					// a double click to the last circle
					IOtask2BlockContext.setDoubleClickFlag(false);

					// reset the exit flag. This is used to prevent multiple attempts to drag circle
					// out of box when it reaches edge
					IOtask2BlockContext.setExitFlag(0);

					// TODO this is where a date should be created if we are going to time the
					// duration of each drag

					// store identity of the clicked circle
					IOtask2BlockContext.setClickedCircle(finalc);
					
					DragContext dC = event.getDragContext();
					
					/*
					// figure out which circle was clicked 
					for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
						double xDist = dC.getDragStartX() - circleGroup[c].getX(); double yDist = dC.getDragStartY() - circleGroup[c].getY();
					 
					  if (Math.pow(xDist, 2) <= Math.pow(circleRadius, 2)) { 
						  if (Math.pow(yDist, 2) <= Math.pow(circleRadius, 2)) { 
							  IOtask2BlockContext.setClickedCircle(c);
						  }
					  }
					} */
				}
			});

			circleGroup[c].addNodeDragMoveHandler(new NodeDragMoveHandler() {
				@Override
				public void onNodeDragMove(NodeDragMoveEvent event) {
					final int clickedCircle = IOtask2BlockContext.getClickedCircle();

					if (clickedCircle == IOtask2BlockContext.getReminderFlag()) {
						IOtask2BlockContext.setReminderFlag(-1);

						if (IOtask2BlockContext.getReminderFlag() == IOtask2BlockContext.getBackupReminderFlag()) {
							IOtask2BlockContext.setBackupReminderFlag(-1);
						}
					}

					if (clickedCircle == IOtask2BlockContext.getBackupReminderFlag()) {
						IOtask2BlockContext.setBackupReminderFlag(-1);
					}

					if (event.getX() <= 0) { // left
						IOtask2BlockContext.setExitFlag(1);
					}

					if (event.getX() >= boxSize) { // right
						IOtask2BlockContext.setExitFlag(2);
					}

					if (event.getY() <= 0) { // up
						IOtask2BlockContext.setExitFlag(3);
					}

					if (event.getY() >= boxSize) { // down
						IOtask2BlockContext.setExitFlag(4);
					}

					if (IOtask2BlockContext.getExitFlag() > 0) {
						DragContext dC = event.getDragContext();

						if (IOtask2BlockContext.getSurpriseTest() <= IOtask2BlockContext.getCompletedCircles()) { // we're
																													// running
																													// a
																													// surprise
																													// test
							if (IOtask2BlockContext.getExitFlag() < 4) { // circle dragged to one of the target
																			// locations
	
								if (IOtask2BlockContext.getSurpriseDrags(IOtask2BlockContext.getExitFlag()) < 1) {
									IOtask2BlockContext.setExitFlag(0);
									Window.alert("You are not allowed to drag any more circles to this side of the box");
								}
							}
						} else { // we're not running a surprise test
							if ((clickedCircle == IOtask2BlockContext.getNextCircle())
									&& ((IOtask2BlockContext.getReminderFlag() == -1)
											|| ((IOtask2BlockContext.getCompletedCircles()
													- IOtask2BlockContext.getReminderCompletedCircles()) < 1))) {

								if (IOtask2BlockContext.getCheckExitFlag() == 1) {
									IOtask2BlockContext.setReminderFlag(IOtask2BlockContext.getBackupReminderFlag());

									IOtask2BlockContext.setReminderCompletedCircles(
											IOtask2BlockContext.getBackupCompletedCircles());
									IOtask2BlockContext.setBackupReminderFlag(-1);
									IOtask2BlockContext.setCheckExitFlag(0);

									int circleNum = clickedCircle + IOtask2BlockContext.getCircleAdjust();

									if (IOtask2BlockContext.getExitFlag() == IOtask2BlockContext
											.getTargetSide(circleNum)) {
										IOtask2BlockContext.incrementHits();
										circles[clickedCircle].setFillColor(ColorName.GREENYELLOW);

										if (IOtask2BlockContext.getShowPointLabels()) {
											if (IOtask2BlockContext.getExitFlag() == 1) {
												leftPoints.addStyleName("greenyellow");

												new Timer() {
													public void run() {
														leftPoints.removeStyleName("greenyellow");
													}
												}.schedule(150);
											}

											if (IOtask2BlockContext.getExitFlag() == 2) {
												rightPoints.addStyleName("greenyellow");

												new Timer() {
													public void run() {
														rightPoints.removeStyleName("greenyellow");
													}
												}.schedule(150);
											}
										}
									} else if (IOtask2BlockContext.getExitFlag() < 4) { // incorrect target response
										circles[clickedCircle].setFillColor(ColorName.RED);
										IOtask2BlockContext.decrementPoints();
									} else { // ongoing response
										//circles[clickedCircle].setFillColor(ColorName.PURPLE);
									}

									circleText[clickedCircle].setVisible(false);

									circleLayer.draw();
								}
							} else {
								if ((IOtask2BlockContext.getFlashFlag() == false) && (IOtask2BlockContext
										.getCompletedCircles() < IOtask2BlockContext.getSurpriseTest())) {
									// flash a circle to indicate the participant has done something wrong
									if (clickedCircle != IOtask2BlockContext.getNextCircle()) {
										circles[IOtask2BlockContext.getNextCircle()].setFillColor(ColorName.RED);
										IOtask2BlockContext.setExitFlag(0);
									} else {
										if (IOtask2BlockContext.getReminderFlag() > -1) {
											circles[IOtask2BlockContext.getReminderFlag()].setFillColor(ColorName.WHITE);
											IOtask2BlockContext.setExitFlag(0);
										}
									}

									circleLayer.draw();

									// then reset it to yellow
									new Timer() {
										public void run() {
											circles[IOtask2BlockContext.getNextCircle()]
													.setFillColor(IOtask2DisplayParams.circleColours[0]);

											if (IOtask2BlockContext.getReminderFlag() > -1) {
												circles[IOtask2BlockContext.getReminderFlag()]
														.setFillColor(IOtask2DisplayParams.circleColours[0]);
											}
											circleLayer.draw();
										}
									}.schedule(400);

									IOtask2BlockContext.setFlashFlag(false);
								}
							}
						}
						
					}

				}
			});

			circleGroup[c].addNodeDragEndHandler((NodeDragEndHandler) new NodeDragEndHandler() {
				@Override
				public void onNodeDragEnd(NodeDragEndEvent event) {
					final int clickedCircle = IOtask2BlockContext.getClickedCircle();

					if (IOtask2BlockContext.getLogDragData()) {
						int circleNum = clickedCircle + IOtask2BlockContext.getCircleAdjust();

						if (clickedCircle < (IOtask2BlockContext.getCompletedCircles()
								% IOtask2BlockContext.getnCircles())) {
							circleNum += IOtask2BlockContext.getnCircles();
						}
							

						//if there's a surprise memory test going on, establish the circle number using a different method
						if (IOtask2BlockContext.getSurpriseTest() <= IOtask2BlockContext.getCompletedCircles()) {
							try {
								circleNum = Integer.parseInt(circleText[clickedCircle].getText()) - 1;		
							}
							
							catch (Exception e) {
								//catch any error due to the circle number being removed from screen
								circleNum = 999;
							}
							
							//also decrement the allowed surprise drags, if appropriate
							if (IOtask2BlockContext.getExitFlag() < 4) { 
								IOtask2BlockContext.decrementSurpriseDrags(IOtask2BlockContext.getExitFlag());
							}
						}			

						String data = "" + IOtask2BlockContext.getBlockNum() + "," + circleNum + ",";
						data = data + IOtask2BlockContext.getTrialNum() + ",";
						data = data + IOtask2BlockContext.getTargetSide(circleNum) + ",";
						data = data + (IOtask2BlockContext.getNextCircle() + IOtask2BlockContext.getCircleAdjust())
								+ ",";
						data = data + IOtask2BlockContext.getExitFlag() + "," + IOtask2BlockContext.getSurpriseTest();

						PHP.logData("dragEnd", data, false);
					}

					AnimationProperties grow = new AnimationProperties();
					grow.push(Properties.SCALE(5));

					final AnimationProperties shrink = new AnimationProperties();
					shrink.push(Properties.SCALE(0));

					// TODO this is where to get another date if we are calculating drag duration

					IOtask2BlockContext.setFlashFlag(false);
					
					// apply the reminder penalty if applicable
					if (IOtask2BlockContext.getReminderPenalty()>0) {
						// get the drag status for each circle so that it can be restored after the
						// lock-out
						final boolean[] dragStatus = new boolean[IOtask2BlockContext.getnCircles()];

						for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
							dragStatus[c] = circleGroup[c].isDraggable();
						}

						if ((IOtask2BlockContext.getExitFlag() == 0)
								& (clickedCircle != IOtask2BlockContext.getNextCircle())) {
							
							int circleNum = clickedCircle + IOtask2BlockContext.getCircleAdjust();
							
							if (clickedCircle < IOtask2BlockContext.getNextCircle()) {
								circleNum += IOtask2BlockContext.getnCircles();
							}
							
							//Window.alert("c: " + circleNum + ", " + IOtask2BlockContext.getTargetSide(circleNum));
							
							if (IOtask2BlockContext.getTargetSide(circleNum)>0) {
								for (int i = 0; i<IOtask2BlockContext.getReminderPenalty(); i++) {
									
									if (IOtask2BlockContext.getSurpriseTest() > IOtask2BlockContext.getCompletedCircles()) {
										//only do this if we're not in a surprise test
										IOtask2BlockContext.decrementPoints();
									}
								}
							}

						}
					}

					// do the reminder lockout if applicable
					if (IOtask2BlockContext.getReminderLockout()) {
						// get the drag status for each circle so that it can be restored after the
						// lock-out
						final boolean[] dragStatus = new boolean[IOtask2BlockContext.getnCircles()];

						for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
							dragStatus[c] = circleGroup[c].isDraggable();
						}

						if ((IOtask2BlockContext.getExitFlag() == 0)
								& (clickedCircle != IOtask2BlockContext.getNextCircle())) {
							for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
								circleGroup[c].setDraggable(false);
								circles[c].setAlpha(0.3);
							}

							circleLayer.draw();

							new Timer() {
								public void run() {
									for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
										circleGroup[c].setDraggable(dragStatus[c]);
										circles[c].setAlpha(1);
									}

									circleLayer.draw();
								}
							}.schedule(IOtask2BlockContext.getReminderLockoutTime());
						}
					}

					new Timer() {
						public void run() {
							pointsDisplay.setHTML("You have scored " + IOtask2BlockContext.getTotalPoints()
									+ " points (" + IOtask2BlockContext.getMoneyString() + ")");
						}
					}.schedule(10);
					
					if (IOtask2BlockContext.getExitFlag() > 0) {			
						IOtask2BlockContext.incrementCompletedCircles();

						if ((IOtask2BlockContext.getCompletedCircles() % IOtask2BlockContext.getnCircles()) == 0) {
							IOtask2BlockContext.doCircleAdjustment();
						}

						IAnimationHandle handle = circles[clickedCircle].animate(AnimationTweener.LINEAR, shrink, 200);

						//move the circle away, so that it doesn't get accidentally picked up again
						new Timer() {
							public void run() {
								circleGroup[clickedCircle].setX(0);
								circleGroup[clickedCircle].setY(0);
							}
						}.schedule(201);	
						
						boolean moreCircles = false; // more circles to add on screen?

						if ((IOtask2BlockContext.getTotalCircles()
								- IOtask2BlockContext.getCompletedCircles()) >= IOtask2BlockContext.getnCircles()) {
							moreCircles = true;
						}

						if (IOtask2BlockContext.getCompletedCircles() >= IOtask2BlockContext.getSurpriseTest()) {
							circleText[clickedCircle].setText("");
							moreCircles = false;
						}

						if (moreCircles) { // more circles to add on screen
							final int newCircle = IOtask2BlockContext.getCompletedCircles()
									+ IOtask2BlockContext.getnCircles() - 1;

							if (IOtask2BlockContext.getTargetSide(newCircle) > 0) { // new circle is a target
								if (IOtask2BlockContext.getOffloadCondition() == Names.REMINDERS_MANDATORY_TARGETONLY) {
									IOtask2BlockContext.setBackupReminderFlag(clickedCircle);
									IOtask2BlockContext
											.setBackupCompletedCircles(IOtask2BlockContext.getCompletedCircles());

									if ((IOtask2BlockContext.getCompletedCircles()
											- IOtask2BlockContext.getReminderCompletedCircles()) > 1) {
										IOtask2BlockContext.setReminderFlag(clickedCircle);
										IOtask2BlockContext.setReminderCompletedCircles(IOtask2BlockContext.getCompletedCircles());
									}
								}
							}

							new Timer() {
								public void run() {
									int targetStatus = IOtask2BlockContext.getTargetSide(newCircle);

									circles[clickedCircle]
											.setFillColor(IOtask2DisplayParams.circleColours[targetStatus]);
									circleGroup[clickedCircle].setX(circleXref[clickedCircle]);
									circleGroup[clickedCircle].setY(circleYref[clickedCircle]);

									if (IOtask2BlockContext.getOffloadCondition() == Names.REMINDERS_NOTALLOWED) {
										circleGroup[clickedCircle].setDraggable(false);
									}

									if (IOtask2BlockContext.getOffloadCondition() == Names.REMINDERS_VARIABLE) {
										if (!IOtask2BlockContext.getMoveableStatus(targetStatus)) {
											circleGroup[clickedCircle].setDraggable(false);
										}
									}

									AnimationProperties grow = new AnimationProperties();
									grow.push(Properties.SCALE(1));

									IAnimationHandle handle = circles[clickedCircle].animate(AnimationTweener.LINEAR,
											grow, 200);
								}
							}.schedule(300);

							new Timer() {
								public void run() {
									circleText[clickedCircle].setText(labels[newCircle]);

									circleText[clickedCircle].setVisible(true);

									circleLayer.draw();
								}
							}.schedule(400);

							final double startR = IOtask2DisplayParams.circleColours[IOtask2BlockContext
									.getTargetSide(newCircle)].getR();
							
							final double startG = IOtask2DisplayParams.circleColours[IOtask2BlockContext
									.getTargetSide(newCircle)].getG();
							
							final double startB = IOtask2DisplayParams.circleColours[IOtask2BlockContext
									.getTargetSide(newCircle)].getB();
							

							final double endR = (double) IOtask2DisplayParams.circleColours[0].getR();
							final double endG = (double) IOtask2DisplayParams.circleColours[0].getG();
							final double endB = (double) IOtask2DisplayParams.circleColours[0].getB();

							new Timer() {
								public void run() {
									IAnimationCallback callback = new IAnimationCallback() {
										public void onClose(IAnimation callback, IAnimationHandle handle) {

										}

										public void onFrame(IAnimation callback, IAnimationHandle handle) {
											double percent = callback.getPercent();
											double newR = startR + (percent * (endR - startR));
											double newG = startG + (percent * (endG - startG));
											double newB = startB + (percent * (endB - startB));

											int R = (int) newR;
											int G = (int) newG;
											int B = (int) newB;

											Color newColor = new Color(R, G, B);

											if (IOtask2BlockContext.getSurpriseTest() > IOtask2BlockContext
													.getCompletedCircles()) {
												// set the colour if we are not running a surprise test
												circles[clickedCircle].setFillColor(newColor);
												circleLayer.draw();
											}
										}

										public void onStart(IAnimation callback, IAnimationHandle handle) {

										}
									};

									IAnimationHandle handle = circles[clickedCircle].animate(null, null, 400, callback);
								}
							}.schedule(IOtask2BlockContext.getInstructionTime());
						}

						IOtask2BlockContext.setCheckExitFlag(1); // ready for next exit event
						IOtask2BlockContext.setExitFlag(0);
						IOtask2BlockContext.incrementNextCircle();

						// run surprise test
						if (IOtask2BlockContext.getSurpriseTest() == IOtask2BlockContext.getCompletedCircles()) {
							trialTimer.cancel();
							IOtask2BlockContext.setCountdownTime(Params.countdownTime);
							
							final Date endTime = new Date();

							int duration = (int) (endTime.getTime() - trialStart.getTime());

							RootPanel.get().remove(verticalPanel);

							// now set all circles to yellow so that colour instructions do not appear
							// during the surprise test. Also make them draggable so they can be dragged in any order
							for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
								circles[c].setFillColor(IOtask2DisplayParams.circleColours[0]);
								circleGroup[c].setDraggable(true);
							}

							new Timer() {
								public void run() {
									int xDim = Window.getClientWidth();
									int yDim = Window.getClientHeight();
									int minDim = 0; // smaller of the two dimensions

									if (xDim <= yDim) {
										minDim = xDim;
									} else {
										minDim = yDim;
									}

									final int boxSize = (int) (minDim * IOtask2DisplayParams.boxSize);

									final int circleRadius = (int) (boxSize * IOtask2DisplayParams.circleRadius);

									final int spacing = (int) (0.9
											* ((double) boxSize / (double) (IOtask2BlockContext.getnCircles() - 1)));

									// shuffle circles then arrange from top to bottom
									ArrayList<Integer> circles = new ArrayList<Integer>();

									for (int c = 0; c < IOtask2BlockContext.getnCircles(); c++) {
										if (c != IOtask2BlockContext.getClickedCircle()) {
											circles.add(c);
										}
									}

									for (int i = 0; i < circles.size(); i++) {
										Collections.swap(circles, i, Random.nextInt(circles.size()));
									}

									for (int c = 0; c < circles.size(); c++) {
										circleGroup[circles.get(c)].setX(boxSize / 2);
										circleGroup[circles.get(c)]
												.setY((0.05 * boxSize) + (c * spacing) + circleRadius);
									}

									RootPanel.get().add(verticalPanel);
									circleLayer.draw();

								}
							}.schedule(500);
						}

						boolean trialEnded=false; //end of trial
						
						//completed last circle?
						if (IOtask2BlockContext.getCompletedCircles() == IOtask2BlockContext.getTotalCircles()) {
							trialEnded=true; 
						}
						
						// end of surprise test?
						if (IOtask2BlockContext.getCompletedCircles() == (IOtask2BlockContext.getSurpriseTest() + IOtask2BlockContext.getnCircles() - 1)) { 
							trialEnded=true;
						}
			
						if (trialEnded) {
							trialTimer.cancel();
							IOtask2BlockContext.setCountdownTime(Params.countdownTime);

							final Date endTime = new Date();

							int duration = (int) (endTime.getTime() - trialStart.getTime());

							final String data = IOtask2BlockContext.getBlockNum() + ","
									+ IOtask2BlockContext.getTrialNum() + "," + IOtask2BlockContext.currentTargetValue()
									+ "," + IOtask2BlockContext.getnHits() + ","
									+ IOtask2BlockContext.getReminderChoice() + "," + duration;

							new Timer() {
								public void run() {
									RootPanel.get().remove(verticalPanel);
									IOtask2BlockContext.incrementCurrentTrial();

									new Timer() {
										public void run() {
											PHP.logData("postTrial", data, true);
										}
									}.schedule(500);
								}
							}.schedule(500);
						} else {
							if (IOtask2BlockContext.getNextCircle() < IOtask2BlockContext.getnCircles()) {
								circleGroup[IOtask2BlockContext.getNextCircle()].setDraggable(true);
							}
						}
					}
				}
			});
		}

		circleGroup[0].addNodeMouseDoubleClickHandler((NodeMouseDoubleClickHandler) new NodeMouseDoubleClickHandler() {
			@Override
			public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
				IOtask2BlockContext.setDoubleClickFlag(true);
			}
		});

		circleGroup[IOtask2BlockContext.getnCircles() - 1]
				.addNodeMouseDoubleClickHandler(new NodeMouseDoubleClickHandler() {
					public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
						if (IOtask2BlockContext.getDoubleClickFlag()) {
							RootPanel.get().remove(verticalPanel);
							IOtask2BlockContext.incrementCurrentTrial();
							
							//pretend that there were 10 hits
							IOtask2BlockContext.setnHits(10);
							IOtask2BlockContext.setHighValHits(10);

							trialTimer.cancel();
							IOtask2BlockContext.setCountdownTime(Params.countdownTime);
							SequenceHandler.Next();
							return;
						}
					}
				});

		circleGroup[0].setDraggable(true);

		panel.add(circleLayer);
		circleLayer.draw();
	}

}
