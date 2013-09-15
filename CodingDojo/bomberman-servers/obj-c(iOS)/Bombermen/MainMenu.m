//
//  MainMenu.m
//  Bombermen
//
//  Created by Hell_Ghost on 10.09.13.
//  Copyright 2013 __MyCompanyName__. All rights reserved.
//

#import "MainMenu.h"
#define ALERT_NAME 100
#define X_OFFSET 215
#define Y_OFFSET (768-87)
#define TILE_SILE 18
@implementation MainMenu
+ (CCScene*)scene {
	CCScene * scene = [CCScene node];
	[scene addChild:[MainMenu node]];
	return scene;
}

- (id)init
{
    self = [super init];
    if (self) {
		CGSize size = [[CCDirector sharedDirector] winSize];
		CCSprite *background;
		background = [CCSprite spriteWithFile:@"SPACE.png"];
		background.position = ccp(size.width/2, size.height/2);
		background.scaleX = size.width/background.contentSize.width;
		background.scaleY = size.height/background.contentSize.height;
		// add the label as a child to this Layer
		[self addChild: background];

		redrawingObject = [[NSMutableArray alloc] init];
		[BombermanAPI sharedApi].delegate = self;
        UIAlertView *nameAlert = [[[UIAlertView alloc] initWithTitle:@"Enter your name" message:nil delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Done", nil] autorelease];
		nameAlert.alertViewStyle = UIAlertViewStylePlainTextInput;
		nameAlert.tag = ALERT_NAME;
		[nameAlert show];
		
    }
    return self;
}

#pragma mark - UIAlertViewDelegate -
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	switch (alertView.tag) {
		case ALERT_NAME:
			if (buttonIndex) {
				NSString *userName = [alertView textFieldAtIndex:0].text;
				[[BombermanAPI sharedApi] newGameWithUserName:userName];
			}
			break;
			
		default:
			break;
	}
}

#pragma mark - BombermanAPIDelegate -
- (void)stepIsOver {
	if (![BombermanAPI sharedApi].isDead) {
		GameObject *bomber = [BombermanAPI sharedApi].bomber;
		if ([[BombermanAPI sharedApi] nearCountAtX:bomber.x y:bomber.y ofElementsType:WALL,DESTROY_WALL,OTHER_BOMB_BOMBERMAN]==4) {
			[[BombermanAPI sharedApi] setDirection:Idle withAction:YES];
		} else {
			[[BombermanAPI sharedApi] setDirection:Idle withAction:YES];
		}
	}
}

#if DRAW_MODE
- (void)wallDataReceived:(NSArray*)wallData {
	for (GameObject * obj in wallData) {
		CCSprite * wall = [CCSprite spriteWithFile:@"WALL.png"];
		[self addChild:wall];
		wall.position = CGPointMake(X_OFFSET + obj.x*wall.boundingBox.size.width, Y_OFFSET - obj.y*wall.boundingBox.size.height);
	}
}

- (void)clearField {
	for (CCSprite * old in redrawingObject) {
		[old removeFromParentAndCleanup:YES];
	}
	[redrawingObject removeAllObjects];
}

- (void)redrawElemet:(GameObject*)element {
		NSString *fileName;
		CCSprite *sprite;
		int zOrd = 1;
		switch (element.type) {
			case DESTROY_WALL:
				sprite = [CCSprite spriteWithFile:@"DESTROY_WALL.png"];
				break;
			case BOMBERMAN:
				sprite = [CCSprite spriteWithFile:@"BOMBERMAN.png"];
				zOrd = 2;
				break;
			case BOMB_BOMBERMAN: {
				sprite = [CCSprite spriteWithFile:fileName = @"BOMB_TIMER.png"];
				CCSprite *men = [CCSprite spriteWithFile:@"BOMBERMAN.png"];
				[sprite addChild:men];
				men.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
				} break;
			case OTHER_BOMB_BOMBERMAN: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCSprite *men = [CCSprite spriteWithFile:@"BOMBERMAN.png"];
				[sprite addChild:men];
				men.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
				men.color = ccc3(255, 0, 0);
			}
				break;
			case BOMB_TIMER_1: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCLabelTTF *label = [CCLabelTTF labelWithString:@"1" fontName:@"Arial-BoldMT" fontSize:12];
				[sprite addChild:label];
				label.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
			}
				break;
			case BOMB_TIMER_2: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCLabelTTF *label = [CCLabelTTF labelWithString:@"2" fontName:@"Arial-BoldMT" fontSize:12];
				[sprite addChild:label];
				label.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
			}
				break;
			case BOMB_TIMER_3: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCLabelTTF *label = [CCLabelTTF labelWithString:@"3" fontName:@"Arial-BoldMT" fontSize:12];
				[sprite addChild:label];
				label.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
			}
				break;
			case BOMB_TIMER_4: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCLabelTTF *label = [CCLabelTTF labelWithString:@"4" fontName:@"Arial-BoldMT" fontSize:12];
				[sprite addChild:label];
				label.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
			}
				break;
			case BOMB_TIMER_5: {
				sprite = [CCSprite spriteWithFile:@"BOMB_TIMER.png"];
				CCLabelTTF *label = [CCLabelTTF labelWithString:@"5" fontName:@"Arial-BoldMT" fontSize:12];
				[sprite addChild:label];
				label.position = ccp(sprite.contentSize.width/2,sprite.contentSize.height/2);
			}
				break;
			case MEAT_CHOPPER:
				sprite = [CCSprite spriteWithFile:@"MEAT_CHOPPER.png"];
				break;
			case SPACE:
				return;
			case BOOM:
				sprite = [CCSprite spriteWithFile:@"BOOM.png"];
				break;
			case DESTROYED_WALL:
				sprite = [CCSprite spriteWithFile:@"DESTROYED_WALL.png"];
				break;
			case DEAD_BOMBERMAN:
				sprite = [CCSprite spriteWithFile:@"DEAD_BOMBERMAN.png"];
				break;
			case DEAD_MEAT_CHOPPER:
				sprite = [CCSprite spriteWithFile:@"DEAD_MEAT_CHOPPER.png"];
				break;
			case OTHER_BOMBERMAN:
				sprite = [CCSprite spriteWithFile:@"BOMBERMAN.png"];
				zOrd = 2;
				sprite.color = ccc3(255, 0, 0);
				break;
			case WALL:
				return;
			case OTHER_DEAD_BOMBERMAN:
				sprite = [CCSprite spriteWithFile:@"DEAD_BOMBERMAN.png"];
				zOrd = 2;
				sprite.color = ccc3(255, 0, 0);
				break;
			case NONE:
				NSAssert(0, @"Unknow error");
				break;
			
		}
		[self addChild:sprite z:zOrd];
		[redrawingObject addObject:sprite];
		sprite.position = CGPointMake(X_OFFSET + element.x*TILE_SILE, Y_OFFSET - element.y*TILE_SILE);
	
}
#endif


@end
