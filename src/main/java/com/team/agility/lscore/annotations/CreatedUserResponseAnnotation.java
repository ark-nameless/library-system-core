package com.team.agility.lscore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.team.agility.lscore.constants.HttpConsts;
import com.team.agility.lscore.entities.User;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ApiResponses(value = {
    @ApiResponse( responseCode = HttpConsts.HTTP_CREATED_CODE, description = HttpConsts.HTTP_CREATED_DESC, content = {
        @Content(schema = @Schema(implementation = User.class))
    }),
    @ApiResponse( responseCode = HttpConsts.HTTP_CONFLICT_CODE, description = "User with same username already exists")
})
public @interface CreatedUserResponseAnnotation {
    
}
