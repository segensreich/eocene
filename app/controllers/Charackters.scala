/*******************************************************************************
 * Copyright (c) 2014 Christian Garbers.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Simplified BSD License
 * which accompanies this distribution
 * 
 * Contributors:
 *     Christian Garbers - initial API and implementation
 ******************************************************************************/
package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import anorm._
import play.api.db.DB
import eoceneServices.eoceneSqlStrings
import eoceneServices.utilities
import play.api.libs.json.Json
import models.Char
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import securesocial.core._
import eoceneServices.EoceneUser
import play.api.mvc.{ Action, RequestHeader }
import play.api.libs.json.JsNumber
import eoceneServices.eoceneUserService

/**
   * Main controller for modifications on characters
   */
class Charackters(override implicit val env: RuntimeEnvironment[EoceneUser]) 
extends securesocial.core.SecureSocial[EoceneUser] {

    /**
   * Get a character with id
   *
   * @param id
   * @return the character (JSON)
   */
  def getCharacterById(id: Int) = SecuredAction(UserAllowedWithCharacterId(id)){
    DB.withConnection("chars") { implicit c =>
      val char = Char.getCharById(id)
      char match{
        case None => NotFound("")
        case _ => Ok(Json.prettyPrint(Json.toJson(char)))
        .withHeaders(CACHE_CONTROL -> "no-cache")
      }      
    }
  }

    /**
   * Create a character with a name
   *
   * @param name
   * @return char id as Json
   */
  def create(name: String) = SecuredAction {implicit request =>
    DB.withConnection("chars") { implicit c =>
      implicit val user = request.user 
      val char = Char.createCharByName(name) 
      char match{
        case None => BadRequest ("")
        case _ => Created (Json.toJson(JsNumber(char.get)))
        .withHeaders(CACHE_CONTROL -> "no-cache")
      }
    }
  }

  /**
  * Create a character with a random name
  *
  * @param name
  * @return char id as Json
  */
  def createWithoutName() = SecuredAction {implicit request =>
    DB.withConnection("chars") { implicit c =>
      implicit val user = request.user 
      val result = Char.createCharByName(
          eoceneServices.utilities.getRandomName())
      result match{
        case None => BadRequest ("")
        case _ =>  Created (Json.toJson(JsNumber(result.get)))
        .withHeaders(CACHE_CONTROL -> "no-cache")
      }
    }
  }
  
  /**
  * Improve an attribute consuming Legend points
  *
  * @param id character id
  * @param attribute the name of the attribute (eg. dex_level)
  * @return success
  */ 
  def improveAttributeLP(id: Int, attribute: String) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
    val x = Char.updateCharAttributeWithLP(id, attribute, "up")
    Logger.info("%s".format(x))
	   x match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  }
  
  /**
  * Reduce an attrribute gaining Legend Points
  *
  * @param id character id
  * @param attribute the name of the attribute (eg. dex_level)
  * @return success
  */ 
  def corruptAttributeLP(id: Int, attribute: String) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.updateCharAttributeWithLP(id, attribute, "down")match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
  
  /**
  * Improve an attribute consuming Legend points
  *
  * @param id character id
  * @param attribute the name of the attribute (eg. dex_level)
  * @return success
  */ 
  def improveAttributePP(id: Int, attribute: String) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.updateCharAttributeWithPP(id, attribute, "up") match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
  
  /**
  * Reduce an attrribute gaining Legend Points
  *
  * @param id character id
  * @param attribute the name of the attribute (eg. dex_level)
  * @return success
  */ 
  def corruptAttributePP(id: Int, attribute: String) =
    SecuredAction(UserAllowedWithCharacterId(id)) {
	  Char.updateCharAttributeWithPP(id, attribute, "down") match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}

  /**
  * Change (or set) a characters Race
  *
  * @param id character id
  * @param id_race
  * @return success
  */ 
  def changeCharRace(id: Int, id_race: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.changeCharRace(id: Int, id_race: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
 
  /**
  * Advance the character in a given discipline
  *
  * @param id character id
  * @param id_race
  * @return success
  */ 
  def improveCharDiscipline(id: Int, id_discipline: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.improveCharDiscipline(id: Int, id_discipline: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
  /**
  * DisAdvance the character in a given discipline
  *
  * @param id character id
  * @param id_race
  * @return success
  */ 
  def corruptCharDiscipline(id: Int, id_discipline: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.corruptCharDiscipline(id: Int, id_discipline: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
  
  /**
  * Improve the a talent
  *
  * @param id character id
  * @param id_talent
  * @return success
  */ 
  def improveCharTalent(id: Int, id_talent: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.improveCharTalent(id: Int, id_talent: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  	}
  
  /**
  * Reduce a talent
  *
  * @param id character id
  * @param id_talent
  * @return success
  */ 
  def corruptCharTalent(id: Int, id_talent: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.corruptCharTalent(id: Int, id_talent: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Improve a skill (or add it)
  *
  * @param id character id
  * @param id_skill
  * @return success
  */ 
  def improveCharSkill(id: Int, id_skill: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.improveCharSkill(id: Int, id_skill: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Reduce a skill (or remnove it)
  *
  * @param id character id
  * @param id_skill
  * @return success
  */ 
  def corruptCharSkill(id: Int, id_skill: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.corruptCharSkill(id: Int, id_skill: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Learn a spell
  *
  * @param id character id
  * @param id_spell
  * @return success
  */ 
  def learnCharSpell(id: Int, id_spell: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.learnCharSpell(id: Int, id_spell: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Forget a spell
  *
  * @param id character id
  * @param id_spell
  * @return success
  */ 
  def unlearnCharSpell(id: Int, id_spell: Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.unlearnCharSpell(id: Int, id_spell: Int) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Change the name of a character (name is read from the request)
  *
  * @param id character id
  * @return success
  */  
  def changeCharName(id: Int) = SecuredAction(UserAllowedWithCharacterId(id)) 
  {implicit request =>
    val data = request.body.asJson
    val name = (data.get\"name").asOpt[String]
    Char.changeCharName(id, name.get) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
  }

  /**
  * Add armor to the character
  *
  * @param id character id
  * @param id_armor
  * @return success
  */  
  def getArmor(id: Int, id_armor:Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.getArmor(id, id_armor) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  /**
  * Remove armor from the character
  *
  * @param id character id
  * @param id_armor
  * @return success
  */    
  def removeArmor(id: Int, id_armor:Int) = 
    SecuredAction(UserAllowedWithCharacterId(id)){
	    Char.removeArmor(id, id_armor) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Attach a thread to an armor item
  *
  * @param id character id
  * @param id_armor
  * @return success
  */   
  def attachThreadArmor(id: Int, id_armor:Int) = 
	SecuredAction(UserAllowedWithCharacterId(id)) {
	    Char.attachThreadArmor(id, id_armor) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
  
  /**
  * Remove a thread to an armor item
  *
  * @param id character id
  * @param id_armor
  * @return success
  */     
  def removeThreadArmor(id: Int, id_armor:Int) = 
	  SecuredAction(UserAllowedWithCharacterId(id)){
	    Char.removeThreadArmor(id, id_armor) match{
	      case false => BadRequest ("")
	      case _ => Ok("")
	    }
	  }
 
  /**
  * Add a spell to matrix
  *  
  * @param id_spell
  * @param id_char
  * @return Success
  */     
  def Spell2Matrix(id_spell:Int, id_char:Int) = SecuredAction{
      Char.Spell2Matrix(id_spell, id_char) match{
        case false => BadRequest ("")
        case _ => Ok("")
      }      
    }
 
  /**
  * Remove a spell from a  matrix
  *  
  * @param id_spell
  * @param id_char
  * @return Success
  */ 
  def SpellFromMatrix(id_spell:Int, id_char:Int) = SecuredAction{
      Char.SpellFromMatrix(id_spell, id_char) match{
        case false => BadRequest ("")
        case _ => Ok("")
      }
  }
  
  /**
  * Return the dice string corresponding to the step number
  *
  * @param step 
  * @return Dice string as JSON
  */   	    
  def getDice(step:Int) = SecuredAction{
      val dice = utilities.getDiceForStep(step)
      dice match{
        case None => BadRequest ("")
        case _ => Ok(Json.toJson(JsString(dice.get))) 
        		  .withHeaders(CACHE_CONTROL -> "no-cache")
      }      
    }
  
  /**
  * Return the result of rolling a dice with the indicated number of sides
  *
  * @param dice the sides of the dice
  * @return Rsult as JSON
  */   	
  def rollDice(dice:Int) = SecuredAction{
      val result:Int = utilities.rollDice(dice)
      Ok(Json.toJson(JsNumber(result))).withHeaders(CACHE_CONTROL -> "no-cache")
    } 
  
  /**
  * Return the result of rolling dice according to the dice string
  * 
  * 
  * Each die starts with a number indicating how often that dice should be 
  * thrown. It is fellowd by the lower case letter d which in turn is fellowed 
  * by the number of sides
  *
  * @param dieces A string modeeling dices 
  * @return Result as JSON
  */   	 
  def rollDiceString(dices:String) = SecuredAction{
    try{
      val result:Int = utilities.rollDiceString(dices)
      Ok(Json.toJson(JsNumber(result))).withHeaders(CACHE_CONTROL -> "no-cache")
    }
    catch{
      case e: Exception => BadRequest ("")
    	}      
    }
  
    
  /**
  * Redirects the user from / to /auth/login when not logged in and
  * to /chars when logged in. Thsi prevents the old firefox credentiels Bug
  *   
  * @return Redirect
  */   	 
  def redirect_user = UserAwareAction{implicit request =>
    request.user match {
      case Some(user) => Redirect("/chars/")
      case _ => Redirect("/auth/login")
    } 
  }

  /**
  * Remove the current user from the users allowed to edit a character
  * 
  * @param id_char    
  * @return Redirect
  */   	 
  def removeUserFromChar(id_char:Int) = 
    SecuredAction(UserAllowedWithCharacterId(id_char)){implicit request =>
    	Char.removeUserFromChar(id_char, request.user.main.userId) match{
    	  case false => BadRequest("")
    	  case true => Ok("")
    	}     
    }
}

case class UserAllowedWithCharacterId(char_id:Int) extends Authorization[EoceneUser] {
  def isAuthorized(user: EoceneUser, request: RequestHeader) = {
	  eoceneUserService.userAllowdOnChar(user.main.userId, char_id)
  }
}
